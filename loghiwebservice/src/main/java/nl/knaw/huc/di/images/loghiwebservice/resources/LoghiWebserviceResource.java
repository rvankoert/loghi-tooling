package nl.knaw.huc.di.images.loghiwebservice.resources;

import com.codahale.metrics.annotation.Timed;
import org.json.simple.JSONObject;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.TreeMap;
import java.util.concurrent.Future;

public class LoghiWebserviceResource {
    protected final TreeMap<String, Future<?>> statusLedger;
    protected int ledgerSize;

    LoghiWebserviceResource(int ledgerSize) {
        this.statusLedger = new TreeMap<>();
        this.ledgerSize = ledgerSize;
    }

    @PermitAll
    @Path("/status/{identifier}")
    @GET
    @Timed
    public Response getStatus(@PathParam("identifier") String identifier) {
        Future<?> job = this.statusLedger.get(identifier);
        JSONObject json = new JSONObject();
        json.put("identifier", identifier);
        if (job == null) {
            json.put("status", "not found");
        }else if (job.isDone()){
            json.put("status", "done");
        } else if (job.isCancelled()){
            json.put("status", "cancelled");
        } else {
            json.put("status", "running or in queue");
        }

        return Response.ok(json.toJSONString()).build();
    }
}
