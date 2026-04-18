package resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Discovery endpoint – returns available top-level API links.
 * Accessible at GET /api/v1
 */
@Path("/")
public class DiscoveryResource {

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response discover() {
        String base = uriInfo.getBaseUri().toString();

        Map<String, String> links = new LinkedHashMap<>();
        links.put("rooms",   base + "rooms");
        links.put("sensors", base + "sensors");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("version", "v1");
        body.put("description", "Smart Campus REST API");
        body.put("_links", links);

        return Response.ok(body).build();
    }
}
