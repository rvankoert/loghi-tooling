package nl.knaw.huc.di.images.loghiwebservice.resources;

import com.codahale.metrics.annotation.Timed;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.minions.MinionExtractBaselines;
import nl.knaw.huc.di.images.minions.MinionSplitPageXMLTextLineIntoWords;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Path("split-page-xml-text-line-into-words")
public class SplitPageXMLTextLineIntoWordsResource {

    private final AtomicLong counter;
    private final String serverUploadLocationFolder;
    private ExecutorService executorService;
    private final StringBuffer minionErrorLog;


    public SplitPageXMLTextLineIntoWordsResource(ExecutorService executorService, String serverUploadLocationFolder) {
        this.serverUploadLocationFolder = serverUploadLocationFolder;
        this.executorService = executorService;
        this.minionErrorLog = new StringBuffer();
        this.counter = new AtomicLong();
    }

    @POST
    @Timed
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(FormDataMultiPart multiPart) {
        if (minionErrorLog.length() > 0) {
            return Response.serverError().entity("Minion is failing: " + minionErrorLog).build();
        }

        final Map<String, List<FormDataBodyPart>> fields = multiPart.getFields();

        if (!fields.containsKey("xml")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"missing field \\\"xml\\\"\"}").build();
        }

        if (!fields.containsKey("identifier")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"missing field \\\"identifier\\\"\"}").build();
        }

        FormDataBodyPart xmlUpload = multiPart.getField("xml");
        InputStream xmlInputStream = xmlUpload.getValueAs(InputStream.class);
        FormDataContentDisposition xmlContentDispositionHeader = xmlUpload.getFormDataContentDisposition();
        String xmlFile = xmlContentDispositionHeader.getFileName();

        final String xml_string;
        try {
            xml_string = IOUtils.toString(xmlInputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Response.serverError().entity("{\"message\":\"Could not read page xml\"}").build();
        }

        Supplier<PcGts> pageSupplier = () -> PageUtils.readPageFromString(xml_string);

        final String identifier = multiPart.getField("identifier").getValue();
        final String outputFile = Paths.get(serverUploadLocationFolder, identifier, xmlFile).toAbsolutePath().toString();

        Runnable job = new MinionSplitPageXMLTextLineIntoWords(identifier, pageSupplier, outputFile, error -> minionErrorLog.append(error).append("\n"));

        try {
            executorService.execute(job);
        } catch (RejectedExecutionException e) {
            return Response.status(Response.Status.TOO_MANY_REQUESTS).entity("{\"message\":\"Queue is full\"}").build();
        }

        long id = counter.incrementAndGet();

        String output = "Files uploaded : " + xmlFile;
        return Response.ok(output).build();
    }
}
