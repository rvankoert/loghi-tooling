package nl.knaw.huc.di.images.loghiwebservice.resources;

import com.codahale.metrics.annotation.Timed;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.minions.MinionExtractBaselines;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

@Path("/extract-baselines")
@Produces(MediaType.APPLICATION_JSON)
public class ExtractBaselinesResource {
    private final AtomicLong counter;

    private final String serverUploadLocationFolder;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractBaselinesResource.class);
    private final StringBuffer minionErrorLog;
    private final String p2alaConfigFile;

    private int maxCount = -1;
    private int margin = 50;
    private ExecutorService executorService;

    public ExtractBaselinesResource(ExecutorService executorService, String serverUploadLocationFolder, String p2alaConfigFile) {
        this.p2alaConfigFile = p2alaConfigFile;
        this.counter = new AtomicLong();
        this.serverUploadLocationFolder = serverUploadLocationFolder;
        this.executorService = executorService;
        this.minionErrorLog = new StringBuffer();
    }


    @POST
    @Timed
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(FormDataMultiPart multiPart) {
        if (minionErrorLog.length() > 0) {
            return Response.serverError().entity("Minion is failing: " + minionErrorLog).build();
        }

        final Map<String, List<FormDataBodyPart>> fields = multiPart.getFields();
        if (!fields.containsKey("mask")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"missing field \\\"mask\\\"\"}").build();
        }

        if (!fields.containsKey("xml")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"missing field \\\"xml\\\"\"}").build();
        }

        if (!fields.containsKey("identifier")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"missing field \\\"identifier\\\"\"}").build();
        }

        FormDataBodyPart maskUpload = multiPart.getField("mask");
        InputStream maskInputStream = maskUpload.getValueAs(InputStream.class);
        FormDataContentDisposition maskContentDispositionHeader = maskUpload.getFormDataContentDisposition();
        String maskFile = maskContentDispositionHeader.getFileName();

        final byte[] array;
        try {
            array = IOUtils.toByteArray(maskInputStream);
        } catch (IOException e) {
            return Response.serverError().entity("{\"message\":\"Could not read image\"}").build();
        }
        Supplier<Mat> imageSupplier = () -> Imgcodecs.imdecode(new MatOfByte(array), Imgcodecs.IMREAD_GRAYSCALE);


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

        final String outputFile = Paths.get(serverUploadLocationFolder, identifier, "extract_baselines.xml").toAbsolutePath().toString();
        Runnable job = new MinionExtractBaselines(identifier, pageSupplier, outputFile, true, p2alaConfigFile, imageSupplier, margin, false, error -> minionErrorLog.append(error).append("\n"));

        try {
            executorService.execute(job);
        } catch (RejectedExecutionException e) {
            return Response.status(Response.Status.TOO_MANY_REQUESTS).entity("{\"message\":\"Queue is full\"}").build();
        }

        long id = counter.incrementAndGet();

        String output = "Files uploaded : " + maskFile + ", " + xmlFile;
        return Response.ok(output).build();
    }
}
