package nl.knaw.huc.di.images.loghiwebservice.resources;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("loghi-htr-merge-page-xml")
public class LoghiHTRMergePageXMLResource {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response schedule(FormDataMultiPart multiPart) {


        return Response.noContent().build();
    }
}
