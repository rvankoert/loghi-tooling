package nl.knaw.huc.di.images.loghiwebservice.resources;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.annotation.Timed;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.minions.MinionExtractBaselines;
import nl.knaw.huc.di.images.minions.MinionExtractBaselines2;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import org.opencv.core.*;
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
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.io.*;
import java.util.function.Supplier;

@Path("/minion-extract-baselines")
@Produces(MediaType.APPLICATION_JSON)
public class MinionExtractBaselinesResource {
    private final AtomicLong counter;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static final String SERVER_UPLOAD_LOCATION_FOLDER = "/tmp/upload/";
    private static final Logger LOGGER = LoggerFactory.getLogger(MinionExtractBaselinesResource.class);

    public MinionExtractBaselinesResource() {
        this.counter = new AtomicLong();
    }

    int numthreads = 1;
    int maxCount = -1;
    int margin = 50;
    ExecutorService executor = Executors.newFixedThreadPool(numthreads);

    @POST
    @Timed
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(FormDataMultiPart multiPart) throws IOException {
        // TODO make identifier a variable uit form
        // TODO velden form controleren en bed request terug geven als form niet goed is
        // TODO queue vol error 500 https://stackoverflow.com/questions/2265869/elegantly-implementing-queue-length-indicators-to-executorservices
        // TODO wegschrijven error 500
        // TODO netjes afsluiten execitor service https://www.dropwizard.io/en/latest/manual/core.html

        FormDataBodyPart maskUpload = multiPart.getField("mask");
        InputStream maskInputStream = maskUpload.getValueAs(InputStream.class);
        FormDataContentDisposition maskContentDispositionHeader = maskUpload.getFormDataContentDisposition();
        String maskFile = maskContentDispositionHeader.getFileName();
        //TODO: uploadFileLocation should come from config.yml

        byte[] array = IOUtils.toByteArray(maskInputStream);
        Supplier<Mat> imageSupplier = () -> Imgcodecs.imdecode(new MatOfByte(array), Imgcodecs.IMREAD_GRAYSCALE);


        FormDataBodyPart xmlUpload = multiPart.getField("xml");
        InputStream xmlInputStream = xmlUpload.getValueAs(InputStream.class);
        FormDataContentDisposition xmlContentDispositionHeader = xmlUpload.getFormDataContentDisposition();
        String xmlFile = xmlContentDispositionHeader.getFileName();

        String xml_string =  IOUtils.toString(xmlInputStream, StandardCharsets.UTF_8);
        Supplier<PcGts> pageSupplier = () -> PageUtils.readPageFromString(xml_string);


        Runnable job = new MinionExtractBaselines(maskFile, pageSupplier, SERVER_UPLOAD_LOCATION_FOLDER+ "test.xml", true, imageSupplier, margin, false);

        executor.execute(job);

        long id = counter.incrementAndGet();

        String output = "Files uploaded : " + maskFile + ", " + xmlFile;
        return Response.ok(output).build();
    }
}
