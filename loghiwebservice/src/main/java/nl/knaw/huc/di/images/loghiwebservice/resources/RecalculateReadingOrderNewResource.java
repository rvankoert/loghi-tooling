package nl.knaw.huc.di.images.loghiwebservice.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("recalculate-reading-order-new")
public class RecalculateReadingOrderNewResource {

    @POST
    public Response schedule() {
        return Response.noContent().build();
    }
}
