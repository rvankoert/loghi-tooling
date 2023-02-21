package nl.knaw.huc.di.images.loghiwebservice.resources;

import com.codahale.metrics.annotation.Timed;
import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.LayoutProc;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.minions.MinionCutFromImageBasedOnPageXMLNew;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

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
import java.util.function.Supplier;

@Path("cut-from-image-based-on-page-xml-new")
public class CutFromImageBasedOnPageXMLNewResource {

    private final ExecutorService cutFromImageExecutorService;
    private final String uploadLocation;

    public CutFromImageBasedOnPageXMLNewResource(ExecutorService cutFromImageExecutorService, String uploadLocation) {

        this.cutFromImageExecutorService = cutFromImageExecutorService;
        this.uploadLocation = uploadLocation;
    }

    @POST
    @Timed
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response schedule(FormDataMultiPart multiPart) {
        final Map<String, List<FormDataBodyPart>> fields = multiPart.getFields();
        if (!fields.containsKey("image")) {
            return missingFieldResponse("image");
        }

        if (!fields.containsKey("page")) {
            return missingFieldResponse("page");
        }

        if (!fields.containsKey("identifier")) {
            return missingFieldResponse("identifier");
        }

        if (!fields.containsKey("output_type")) {
            return missingFieldResponse("output_type");
        }

        if (!fields.containsKey("channels")) {
            return missingFieldResponse("channels");
        }

        FormDataBodyPart maskUpload = multiPart.getField("image");
        InputStream maskInputStream = maskUpload.getValueAs(InputStream.class);
        FormDataContentDisposition maskContentDispositionHeader = maskUpload.getFormDataContentDisposition();
        String imageFile = maskContentDispositionHeader.getFileName();
        final byte[] array;
        try {
            array = IOUtils.toByteArray(maskInputStream);
        } catch (IOException e) {
            return Response.serverError().entity("{\"message\":\"Could not read image\"}").build();
        }
        Supplier<Mat> imageSupplier = () -> Imgcodecs.imdecode(new MatOfByte(array), Imgcodecs.IMREAD_COLOR);

        FormDataBodyPart xmlUpload = multiPart.getField("page");
        InputStream xmlInputStream = xmlUpload.getValueAs(InputStream.class);
        FormDataContentDisposition xmlContentDispositionHeader = xmlUpload.getFormDataContentDisposition();
        String pageFile = xmlContentDispositionHeader.getFileName();

        final String xml_string;
        try {
            xml_string = IOUtils.toString(xmlInputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Response.serverError().entity("{\"message\":\"Could not read page xml\"}").build();
        }

        Supplier<PcGts> pageSupplier = () -> PageUtils.readPageFromString(xml_string);
        final String identifier = multiPart.getField("identifier").getValue();
        final String outputBase = Paths.get(uploadLocation).toAbsolutePath().toString();
        final String outputType = multiPart.getField("output_type").getValue();
        final int channels = multiPart.getField("channels").getValueAs(Integer.class);
        final boolean overwriteExistingPage = false;
        final MinionCutFromImageBasedOnPageXMLNew minionCutFromImageBasedOnPageXMLNew = new MinionCutFromImageBasedOnPageXMLNew(identifier, imageSupplier, pageSupplier, outputBase, overwriteExistingPage, 5, 5, 0, outputType, channels, false, null, true, true, true, null, LayoutProc.MINIMUM_XHEIGHT, false, false, false);
        cutFromImageExecutorService.execute(minionCutFromImageBasedOnPageXMLNew);

        String output = "Files uploaded : " + imageFile + ", " + pageFile;

        return Response.ok(output).build();
    }

    private Response missingFieldResponse(String output_type) {
        return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"missing field \\\"" + output_type + "\\\"\"}").build();
    }

}
