package nl.knaw.huc.di.images.loghiwebservice.resources;

import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.minions.MinionRecalculateReadingOrderNew;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

@Path("recalculate-reading-order-new")
public class RecalculateReadingOrderNewResource {

    public static final Logger LOG = LoggerFactory.getLogger(RecalculateReadingOrderNewResource.class);
    private final ExecutorService recalculateReadingOrderNewResourceExecutorService;
    private final String uploadLocation;
    private final StringBuilder errorLog;

    public RecalculateReadingOrderNewResource(ExecutorService recalculateReadingOrderNewResourceExecutorService, String uploadLocation) {

        this.recalculateReadingOrderNewResourceExecutorService = recalculateReadingOrderNewResourceExecutorService;
        this.uploadLocation = uploadLocation;
        errorLog = new StringBuilder();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response schedule(FormDataMultiPart form) {
        if (errorLog.length() > 0) {
            return Response.serverError().entity("Minion is failing: " + errorLog).build();
        }

        final Set<String> fields = form.getFields().keySet();

        if(!fields.contains("page")) {
            return missingFieldResponse("page");
        }

        if (!fields.contains("border_margin")) {
           return missingFieldResponse("border_margin");
        }

        if (!fields.contains("identifier")) {
            return missingFieldResponse("identifier");
        }

        final int borderMargin = form.getField("border_margin").getValueAs(Integer.class);
        final String identifier = form.getField("identifier").getValue();

        final Consumer<PcGts> pageSaver = page -> {
            final java.nio.file.Path targetFile = Paths.get(uploadLocation, identifier, "recalculate-reading-order.xml");
            try {
                if (!Files.exists(targetFile.getParent())) {
                    Files.createDirectories(targetFile.getParent());
                }
                String pageXmlString = PageUtils.convertPcGtsToString(page);
                StringTools.writeFile(targetFile, pageXmlString, false);
            } catch (IOException e) {
                LOG.error("Could not save page: {}", targetFile, e);
                errorLog.append("Could not save page: ").append(targetFile).append("\n");
            }
        };

        FormDataBodyPart xmlUpload = form.getField("page");
        InputStream xmlInputStream = xmlUpload.getValueAs(InputStream.class);
        final String xmlString;
        try {
            xmlString = IOUtils.toString(xmlInputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Response.serverError().entity("{\"message\":\"Could not read page xml\"}").build();
        }

        final PcGts page = PageUtils.readPageFromString(xmlString);

        final MinionRecalculateReadingOrderNew job = new MinionRecalculateReadingOrderNew(identifier, page, pageSaver, false, borderMargin);
        recalculateReadingOrderNewResourceExecutorService.execute(job);

        return Response.noContent().build();
    }

    private Response missingFieldResponse(String field) {
        return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"missing field \\\"" + field + "\\\"\"}").build();
    }
}
