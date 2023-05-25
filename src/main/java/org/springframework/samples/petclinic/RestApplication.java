package org.springframework.samples.petclinic;

import jakarta.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

@OpenAPIDefinition(
    info = @Info(
        title="REST Petclinic backend Api Documentation",
        description = "This is REST API documentation of the Spring Petclinic backend. If authentication is enabled, when calling the APIs use admin/admin",
        version = "1.0", 
        termsOfService = "Petclinic backend terms of service",
        contact = @Contact(
            name = "Maintainer",
            url = "aytartana.wordpress.com",
            email = "jonathan.vila@gmail.com"),
        license = @License(
            name = "Apache 2.0",
            url = "http://www.apache.org/licenses/LICENSE-2.0.html"))
)
public class RestApplication extends Application {
}