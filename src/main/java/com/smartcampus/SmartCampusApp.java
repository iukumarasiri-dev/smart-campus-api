package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api/v1")
public class SmartCampusApp extends Application {
    // JAX-RS will auto-scan and register all resources
}
