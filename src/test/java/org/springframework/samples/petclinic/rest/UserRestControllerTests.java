package org.springframework.samples.petclinic.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import jakarta.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.service.UserService;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)

public class UserRestControllerTests {

    @Mock
    private UserService userService;

    @Test
    public void testCreateUserSuccess() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEnabled(true);
        user.addRole( "OWNER_ADMIN" );
        ObjectMapper mapper = new ObjectMapper();
        String newVetAsJSON = mapper.writeValueAsString(user);
        given()
			.auth().basic("admin", "admin")
		.when()
          .post("/api/users/")
        .then()
            .body(equalTo(newVetAsJSON))
            .contentType(ContentType.JSON)
            .statusCode(Status.CREATED.getStatusCode());
    }

    @Test
    public void testCreateUserError() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEnabled(true);
        ObjectMapper mapper = new ObjectMapper();
        String newVetAsJSON = mapper.writeValueAsString(user);
        given()
			.auth().basic("admin", "admin")
		.when()
          .post("/api/users/")
        .then()
            .body(equalTo(newVetAsJSON))
            .contentType(ContentType.JSON)
            .statusCode(Status.BAD_REQUEST.getStatusCode());
    }
}
