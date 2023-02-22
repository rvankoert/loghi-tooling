package nl.knaw.huc.di.images.loghiwebservice.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutorService;

@Path("recalculate-reading-order-new")
public class RecalculateReadingOrderNewResource {

    public RecalculateReadingOrderNewResource(ExecutorService recalculateReadingOrderNewResourceExecutorService, String uploadLocation) {

    }

    @POST
    public Response schedule() {
        return Response.noContent().build();
    }
}
