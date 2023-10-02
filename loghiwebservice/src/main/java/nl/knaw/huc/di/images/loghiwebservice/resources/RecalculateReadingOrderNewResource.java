package nl.knaw.huc.di.images.loghiwebservice.resources;

import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.minions.MinionRecalculateReadingOrderNew;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
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
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Path("recalculate-reading-order-new")
public class RecalculateReadingOrderNewResource {

    public static final Logger LOG = LoggerFactory.getLogger(RecalculateReadingOrderNewResource.class);
    private final ExecutorService recalculateReadingOrderNewResourceExecutorService;
    private final String uploadLocation;
    private final Supplier<String> queueUsageStatusSupplier;
    private final StringBuilder errorLog;

    public RecalculateReadingOrderNewResource(ExecutorService recalculateReadingOrderNewResourceExecutorService, String uploadLocation, Supplier<String> queueUsageStatusSupplier) {

        this.recalculateReadingOrderNewResourceExecutorService = recalculateReadingOrderNewResourceExecutorService;
        this.uploadLocation = uploadLocation;
        this.queueUsageStatusSupplier = queueUsageStatusSupplier;
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
        String namespace = fields.contains("namespace")? form.getField("namespace").getValue() : PageUtils.NAMESPACE2019;

        FormDataBodyPart xmlUpload = form.getField("page");
        final String pageFile = xmlUpload.getFormDataContentDisposition().getFileName();
        InputStream xmlInputStream = xmlUpload.getValueAs(InputStream.class);
        final String xmlString;
        try {
            xmlString = IOUtils.toString(xmlInputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return Response.serverError().entity("{\"message\":\"Could not read page xml\"}").build();
        }

        final Consumer<PcGts> pageSaver = page -> {
            final java.nio.file.Path targetFile = Paths.get(uploadLocation, identifier, pageFile);
            try {
                if (!Files.exists(targetFile.getParent())) {
                    Files.createDirectories(targetFile.getParent());
                }
                PageUtils.writePageToFileAtomic(page, namespace, targetFile);
            } catch (IOException e) {
                LOG.error("Could not save page: {}", targetFile, e);
                errorLog.append("Could not save page: ").append(targetFile).append("\n");
            }
        };

        final PcGts page = PageUtils.readPageFromString(xmlString);

        final double interlineClusteringMultiplier =  fields.contains("interline_clustering_multiplier") ? form.getField("interline_clustering_multiplier").getValueAs(Double.class) :  1.5;
        final double dubiousSizeWidthMultiplier = fields.contains("dubious_size_width_multiplier") ? form.getField("dubious_size_width_multiplier").getValueAs(Double.class): 0.05;
        final Double dubiousSizeWidth = fields.contains("dubious_size_width") ? form.getField("dubious_size_width").getValueAs(Double.class): null;
        final MinionRecalculateReadingOrderNew job = new MinionRecalculateReadingOrderNew(identifier, page, pageSaver,
                false, borderMargin,false, interlineClusteringMultiplier,
                dubiousSizeWidthMultiplier, dubiousSizeWidth, null);
        recalculateReadingOrderNewResourceExecutorService.execute(job);

        return Response.ok("{\"queueStatus\": "+ queueUsageStatusSupplier.get() + "}").build();
    }

    private Response missingFieldResponse(String field) {
        return Response.status(Response.Status.BAD_REQUEST).entity("{\"message\":\"missing field \\\"" + field + "\\\"\"}").build();
    }
}
