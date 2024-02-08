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
import org.jetbrains.annotations.Nullable;
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
        final HashMap<String, String> metadataMap = new HashMap<>();
        final HashMap<String, Double> confidenceMap = new HashMap<>();

        try {
            resultsString = IOUtils.toString(resultsInputStream, StandardCharsets.UTF_8);

            fillDictionary(resultsString, fileTextLineMap, metadataMap, confidenceMap);
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
        Runnable job = new MinionLoghiHTRMergePageXML(identifier, pageSupplier, htrConfig, fileTextLineMap, metadataMap,
                confidenceMap, pageSaver, pageFile, comment, gitHash, Optional.of(errorFileWriter));

        try {
            executorService.execute(job);
        } catch (RejectedExecutionException e) {
            return Response.status(Response.Status.TOO_MANY_REQUESTS)
                    .entity("{\"message\":\"LoghiHTRMergePageXMLResource.java queue is full\"}").build();
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

    private void fillDictionary(String resultsFile,
                                Map<String, String> fileTextLineMap,
                                Map<String, String> metadataMap,
                                Map<String, Double> confidenceMap) throws IOException {

        try (BufferedReader br = new BufferedReader(new StringReader(resultsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                ResultLine resultLine = getResultLine(line);
                if (resultLine == null) continue;
                fileTextLineMap.put(resultLine.filename, resultLine.text.toString().trim());
                metadataMap.put(resultLine.filename, resultLine.metadata.trim());
                confidenceMap.put(resultLine.filename, resultLine.confidence);
                LOG.debug(resultLine.filename + " appended to dictionary");
            }
        }
    }

    public static ResultLine getResultLine(String line) {
        int tabCount = countTabs(line);

        String[] splitted = line.split("\t");
        String filename = splitted[0].split("/")[splitted[0].split("/").length - 1].replace(".png", "").trim();
        double confidence = 1.0;
        String metadata = "[]"; //set base value for metadata
        StringBuilder text = new StringBuilder();


        if (tabCount < 2) {
            // tabCount should be either 2 for old Style and 3 for new style
            LOG.warn("result line htr seems too short: " + line);
            return null;
        }

        if (tabCount == 3) {
            // Format: filename\tmetadata\tconfidence\tpred_text (with pred_text potentially empty)
            metadata = splitted[1];
            try {
                confidence = Double.parseDouble(splitted[2]);
            } catch (Exception ex) {
                ex.printStackTrace();
                LOG.error(filename + ex.getMessage());
            }
            if (splitted.length > 3) { // Check if pred_text is not empty
                text.append(splitted[3]);
            }
        } else if (tabCount == 2) {
            // Format: filename\tconfidence\tpred_text (with pred_text potentially empty)
            try {
                confidence = Double.parseDouble(splitted[1]);
            } catch (Exception ex) {
                ex.printStackTrace();
                LOG.error(filename + ex.getMessage());
            }
            if (splitted.length > 2) { // Check if pred_text is not empty
                text.append(splitted[2]);
            }
        } else {
            throw new IllegalArgumentException("Input line does not match expected formats.");
        }

        ResultLine resultLine;
        if (!metadata.equals("[]")) {
            resultLine = new ResultLine(filename, confidence, metadata, text);
        } else {
            resultLine = new ResultLine(filename, confidence, text);
        }

        return resultLine;
    }

    private static int countTabs(String str) {
        int tabCount = 0;

        // Iterate over each character in the string
        for (int i = 0; i < str.length(); i++) {
            // Check if the current character is a tab
            if (str.charAt(i) == '\t') {
                tabCount++;
            }
        }

        return tabCount;
    }

    public static class ResultLine {
        private final String filename;
        private final double confidence;
        private final StringBuilder text;

        //Metadata init as null for default
        private String metadata = null;

        // Constructor without metadata
        public ResultLine(String filename, double confidence, StringBuilder text) {
            this.filename = filename;
            this.confidence = confidence;
            this.text = text;
        }

        // Constructor with metadata
        public ResultLine(String filename, double confidence, String metadata, StringBuilder text) {
            this(filename, confidence, text); // Calls the other constructor
            this.metadata = metadata; // Sets metadata
        }


        public String getFilename() {
            return filename;
        }

        public double getConfidence() {
            return confidence;
        }

        public StringBuilder getText() {
            return text;
        }

        public String getMetadata(){
            // Return "[]" if metadata is null, otherwise return metadata
            return metadata == null ? "[]" : metadata;
        }
    }

    private Response missingFieldResponse(String field) {
        return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"missing field \\\"" + field + "\\\"\"}").build();
    }
}