package nl.knaw.huc.di.images.loghiwebservice.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("split-page-xml-text-line-into-words")
public class SplitPageXMLTextLineIntoWordsResource {
    @POST
    public Response schedule() {
        return Response.noContent().build();
    }
}
