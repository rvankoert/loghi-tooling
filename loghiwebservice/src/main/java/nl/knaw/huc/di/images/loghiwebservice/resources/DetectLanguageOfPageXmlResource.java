package nl.knaw.huc.di.images.loghiwebservice.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("detect-language-of-page-xml")
public class DetectLanguageOfPageXmlResource {
    @POST
    public Response schedule(){
        return Response.noContent().build();
    }
}
