package nl.knaw.huc.di.images.loghiwebservice.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.knaw.huc.di.images.layoutds.models.HTRConfig;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.minions.MinionLoghiHTRMergePageXML;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huc.di.images.pipelineutils.ErrorFileWriter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.TransformerException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javax.annotation.security.PermitAll;

@Path("loghi-htr-merge-page-xml")
public class LoghiHTRMergePageXMLResource {

    public static final Logger LOG = LoggerFactory.getLogger(LoghiHTRMergePageXMLResource.class);
    private final String uploadLocation;
    private final ExecutorService executorService;
    private final Supplier<String> queueUsageStatusSupplier;
    private final StringBuilder errorLog;
    private final ErrorFileWriter errorFileWriter;

    public LoghiHTRMergePageXMLResource(String uploadLocation, ExecutorService executorService, Supplier<String> queueUsageStatusSupplier) {

        this.uploadLocation = uploadLocation;
        this.executorService = executorService;
        this.queueUsageStatusSupplier = queueUsageStatusSupplier;
        errorLog = new StringBuilder();
        errorFileWriter = new ErrorFileWriter(uploadLocation);
    }

    @PermitAll
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response schedule(FormDataMultiPart multiPart) {
        if (errorLog.length() > 0) {
            return Response.serverError().entity("Minion is failing: " + errorLog).build();
        }

        final Set<String> fieldNames = multiPart.getFields().keySet();

        if (!fieldNames.contains("page")) {
            return missingFieldResponse("page");
        }

        if (!fieldNames.contains("results")) {
            return missingFieldResponse("results");
        }

        if (!fieldNames.contains("htr-config")) {
            return missingFieldResponse("htr-config");
        }

        if (!fieldNames.contains("identifier")) {
            return missingFieldResponse("identifier");
        }

        FormDataBodyPart xmlUpload = multiPart.getField("page");
        FormDataContentDisposition xmlContentDispositionHeader = xmlUpload.getFormDataContentDisposition();
        String pageFile = FilenameUtils.removeExtension(xmlContentDispositionHeader.getFileName());
        String comment;

        InputStream xmlInputStream = xmlUpload.getValueAs(InputStream.class);
        final String xmlString;
        final String identifier = multiPart.getField("identifier").getValue();

        try {
            xmlString = IOUtils.toString(xmlInputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            errorFileWriter.write(identifier,e, "Could not read page xml");
            return Response.serverError().entity("{\"message\":\"Could not read page xml\"}").build();
        }

        Supplier<PcGts> pageSupplier = () -> PageUtils.readPageFromString(xmlString);

        FormDataBodyPart resultsUpload = multiPart.getField("results");
        InputStream resultsInputStream = resultsUpload.getValueAs(InputStream.class);
        final String resultsString;
        final HashMap<String, String> fileTextLineMap = new HashMap<>();
        final HashMap<String, Double> confidenceMap = new HashMap<>();

        try {
            resultsString = IOUtils.toString(resultsInputStream, StandardCharsets.UTF_8);

            fillDictionary(resultsString, fileTextLineMap, confidenceMap);
            LOG.info("lines dictionary contains: " + fileTextLineMap.size());
        } catch (IOException e) {
            LOG.error("Could not read results", e);
            errorFileWriter.write(identifier,e, "Could not read results");
            return Response.serverError().entity("{\"message\":\"Could not read results\"}").build();
        }

        final ObjectMapper objectMapper = new ObjectMapper();
        final HTRConfig htrConfig;
        final String gitHash;
        final List<String> configWhiteList;
        if (fieldNames.contains("config_white_list")) {
            configWhiteList = multiPart.getFields("config_white_list").stream().map(FormDataBodyPart::getValue).collect(Collectors.toList());
        } else {
            configWhiteList = new ArrayList<>();
        }
        try {
            htrConfig = readHtrConfig(multiPart, objectMapper, configWhiteList);
        } catch (Exception e) {
            LOG.error("Error with reading htr-config", e);
            errorFileWriter.write(identifier, e, "Could not read htr-config.");
            return Response.serverError().entity("{\"message\":\"Could not read htr-config\"}").build();
        }
        if (fieldNames.contains("git_hash")) {
            gitHash = multiPart.getField("git_hash").getValue();
        } else {
            gitHash = null;
        }


        String namespace = fieldNames.contains("namespace")? multiPart.getField("namespace").getValue() : PageUtils.NAMESPACE2019;
        if (!PageUtils.NAMESPACE2013.equals(namespace) && ! PageUtils.NAMESPACE2019.equals(namespace)) {
            final String namespaceException = "Unsupported page xml namespace use " + PageUtils.NAMESPACE2013 + " or " + PageUtils.NAMESPACE2019;
            errorFileWriter.write(identifier, "Unsupported page xml namespace.");
            return Response.status(400).entity("{\"message\":\"" + namespaceException + "\"}").build();
        }

        final Consumer<PcGts> pageSaver = page -> {
            final java.nio.file.Path targetFile = Paths.get(uploadLocation, identifier, pageFile + ".xml");
            try {
                if (!Files.exists(targetFile.getParent())) {
                    Files.createDirectories(targetFile.getParent());
                }
                PageUtils.writePageToFileAtomic(page, namespace, targetFile);
            } catch (IOException e) {
                LOG.error("Could not save page: {}", targetFile, e);
                errorLog.append("Could not save page: ").append(targetFile).append("\n");
                errorFileWriter.write(identifier, e, "Could not save page");
            } catch (TransformerException e) {
                LOG.error("Could not transform page to 2013 version", e);
                errorLog.append("Could not transform page to 2013 version: ")
                        .append(e.getMessage());
                errorFileWriter.write(identifier, e, "Could not transform page to 2013 version");
            }
        };

        comment = FormMultipartHelper.getFieldOrDefaultValue(String.class, multiPart, multiPart.getFields(),
                "comment", "");
        Runnable job = new MinionLoghiHTRMergePageXML(identifier, pageSupplier, htrConfig, fileTextLineMap,
                confidenceMap, pageSaver, pageFile, comment, gitHash, Optional.of(errorFileWriter));

        try {
            executorService.execute(job);
        } catch (RejectedExecutionException e) {
            return Response.status(Response.Status.TOO_MANY_REQUESTS)
                    .entity("{\"message\":\"LoghiHTRMergePageXMLResource queue is full\"}").build();
        }

        return Response.ok("{\"queueStatus\": " + queueUsageStatusSupplier.get() + "}").build();
    }

    private HTRConfig readHtrConfig(FormDataMultiPart multiPart, ObjectMapper objectMapper, List<String> configWhiteList) throws IOException {
        final HTRConfig htrConfig = new HTRConfig();
        final ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(multiPart.getField("htr-config").getValueAs(InputStream.class));

        String gitHash = jsonNode.get("git_hash").asText();
        String model = jsonNode.get("model").asText();

        htrConfig.setModel(model);
        htrConfig.setGithash(gitHash);
        if (jsonNode.has("uuid")) {
            htrConfig.setUuid(UUID.fromString(jsonNode.get("uuid").asText()));
        }

        final HashMap<String, Object> values = new HashMap<>();
        final JsonNode args = jsonNode.get("args");
        for (final Iterator<Map.Entry<String, JsonNode>> fields = args.fields(); fields.hasNext(); ) {
            final Map.Entry<String, JsonNode> field = fields.next();
            if (configWhiteList.contains(field.getKey())) {
                values.put(field.getKey(), field.getValue().asText());
            }
        }

        htrConfig.setValues(values);
        return htrConfig;
    }

    private void fillDictionary(String resultsFile, Map<String, String> fileTextLineMap, Map<String, Double> confidenceMap) throws IOException {

        try (BufferedReader br = new BufferedReader(new StringReader(resultsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split("\t");
                if (splitted.length < 3) {
                    LOG.warn("result line htr seems too short: " + line);
                    continue;
                }
                String filename = splitted[0];
                double confidence = 0;

                try {
                    confidence = Double.parseDouble(splitted[1]);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    LOG.error(filename + ex.getMessage());
                }
                StringBuilder text = new StringBuilder();
                for (int i = 2; i < splitted.length; i++) {
                    text.append(splitted[i]);//line.substring(filename.length() + 1);
                    text.append("\t");
                }
                text = new StringBuilder(text.toString().trim());
                splitted = filename.split("/");
                filename = splitted[splitted.length - 1].replace(".png", "").trim();
                fileTextLineMap.put(filename, text.toString().trim());
                confidenceMap.put(filename, confidence);
                LOG.debug(filename + " appended to dictionary");
            }
        }
    }

    private Response missingFieldResponse(String field) {
        return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"missing field \\\"" + field + "\\\"\"}").build();
    }
}
