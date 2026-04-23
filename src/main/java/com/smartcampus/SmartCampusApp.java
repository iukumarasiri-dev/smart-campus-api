package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.server.ServerProperties;
import java.util.HashMap;
import java.util.Map;

@ApplicationPath("/api/v1")
public class SmartCampusApp extends Application {

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        // Use setStatus() instead of sendError() so Tomcat does not replace
        // error responses with its own HTML error pages
        properties.put(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, true);
        return properties;
    }
}
