package nl.knaw.huc.di.images.loghiwebservice.resources;

import com.codahale.metrics.annotation.Timed;
import nl.knaw.huc.di.images.layoutds.models.LaypaConfig;
import nl.knaw.huc.di.images.layoutds.models.P2PaLAConfig;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.minions.MinionExtractBaselines;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.json.simple.parser.ParseException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Path("/extract-baselines")
@Produces(MediaType.APPLICATION_JSON)
public class ExtractBaselinesResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractBaselinesResource.class);
    private final AtomicLong counter;
    private final String serverUploadLocationFolder;
    private final StringBuffer minionErrorLog;
    private final String p2palaConfigFile;
    private final String laypaConfigFile;
    private final Supplier<String> queueUsageStatusSupplier;

    private int maxCount = -1;
    private int margin = 50;
    private ExecutorService executorService;

    public ExtractBaselinesResource(ExecutorService executorService, String serverUploadLocationFolder, String p2palaConfigFile, String laypaConfigFile, Supplier<String> queueUsageStatusSupplier) {
        this.p2palaConfigFile = p2palaConfigFile;
        this.laypaConfigFile = laypaConfigFile;
        this.queueUsageStatusSupplier = queueUsageStatusSupplier;
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

        int margin = this.margin;
        if (fields.containsKey("margin")) {
            margin = multiPart.getField("margin").getValueAs(Integer.class);
        }

        List<String> reorderRegionsList = new ArrayList<>();

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
        int threshold = 32;
        final String outputFile = Paths.get(serverUploadLocationFolder, identifier, xmlFile).toAbsolutePath().toString();
        final boolean invertImage = fields.containsKey("invertImage") && multiPart.getField("invertImage").getValue().equals("true");
        LaypaConfig laypaConfig = null;

        P2PaLAConfig p2palaconfig = null;
        if (invertImage) {
            try {
                if (fields.containsKey("p2pala_config")) {
                    final InputStream p2palaConfigInputStream = multiPart.getField("p2pala_config").getValueAs(InputStream.class);
                    p2palaconfig = MinionExtractBaselines.readP2PaLAConfigFile(p2palaConfigInputStream);

                } else {
                    p2palaconfig = MinionExtractBaselines.readP2PaLAConfigFile(p2palaConfigFile);
                }
            } catch (IOException | ParseException e) {
                LOGGER.error("Could not read p2palaConfig");
            }
        } else {
            try {
                if (fields.containsKey("laypa_config")) {
                    final InputStream laypaConfigInputStream = multiPart.getField("laypa_config").getValueAs(InputStream.class);
                    laypaConfig = MinionExtractBaselines.readLaypaConfigFile(laypaConfigInputStream);
                } else {
                    laypaConfig = MinionExtractBaselines.readLaypaConfigFile(laypaConfigFile);
                }
            } catch (IOException | ParseException e) {
                LOGGER.error("Could not read laypaConfigFile: {}", laypaConfigFile);
            }
        }
        Runnable job = new MinionExtractBaselines(identifier, pageSupplier, outputFile,
                true, p2palaconfig, laypaConfig,  imageSupplier, margin, invertImage,
                error -> minionErrorLog.append(error).append("\n"),
                threshold, reorderRegionsList);

        try {
            executorService.execute(job);
        } catch (RejectedExecutionException e) {
            return Response.status(Response.Status.TOO_MANY_REQUESTS).entity("{\"message\":\"Queue is full\"}").build();
        }

        long id = counter.incrementAndGet();

        String output = "{\"filesUploaded\": [\"" + maskFile + "\", \"" + xmlFile + "\"]," +
                "\"queueStatus\": "+ queueUsageStatusSupplier.get() + "}";
        return Response.ok(output).build();
    }
}
