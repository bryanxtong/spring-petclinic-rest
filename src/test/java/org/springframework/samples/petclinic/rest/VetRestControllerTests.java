/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.service.ClinicService;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;

/**
 * Test class for {@link VetRestController}
 *
 * @author Vitaliy Fedoriv
 */
@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)

public class VetRestControllerTests {

	@InjectMock
    private ClinicService clinicService;

    private List<Vet> vets;

    @BeforeEach
    public void initVets(){
    	vets = new ArrayList<Vet>();


    	Vet vet = new Vet();
    	vet.setId(1);
    	vet.setFirstName("James");
    	vet.setLastName("Carter");
    	vets.add(vet);

    	vet = new Vet();
    	vet.setId(2);
    	vet.setFirstName("Helen");
    	vet.setLastName("Leary");
    	vets.add(vet);

    	vet = new Vet();
    	vet.setId(3);
    	vet.setFirstName("Linda");
    	vet.setLastName("Douglas");
    	vets.add(vet);
    }

    @Test
    public void testGetVetSuccess() throws Exception {
    	given(this.clinicService.findVetById(1)).willReturn(vets.get(0));
        given()
			.auth().basic("admin", "admin")
		.when()
		  .get("/api/vets/1")
		.then()
			.contentType(ContentType.JSON)
            .statusCode(Status.OK.getStatusCode())
            .body("id", equalTo(1))
            .body("firstName", equalTo("James"));
    }

    @Test
    public void testGetVetNotFound() throws Exception {
    	given(this.clinicService.findVetById(-1)).willReturn(null);
        given()
			.auth().basic("admin", "admin")
		.when()
		  .get("/api/vets/-1")
		.then()
			.contentType(ContentType.JSON)
            .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testGetAllVetsSuccess() throws Exception {
    	given(this.clinicService.findAllVets()).willReturn(vets);
        given()
			.auth().basic("admin", "admin")
		.when()
		  .get("/api/vets/")
		.then()
			.contentType(ContentType.JSON)
            .statusCode(Status.OK.getStatusCode())
            .body("[0].id", equalTo(1))
            .body("[0].firstName", equalTo("James"))
            .body("[1].id", equalTo(2))
            .body("[1].firstName", equalTo("Helen"));
    }

    @Test
    public void testGetAllVetsNotFound() throws Exception {
    	vets.clear();
    	given(this.clinicService.findAllVets()).willReturn(vets);
        given()
			.auth().basic("admin", "admin")
		.when()
		  .get("/api/vets/")
		.then()
			.contentType(ContentType.JSON)
            .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testCreateVetSuccess() throws Exception {
    	Vet newVet = vets.get(0);
    	newVet.setId(999);
    	ObjectMapper mapper = new ObjectMapper();
    	String newVetAsJSON = mapper.writeValueAsString(newVet);
    	given()
			.auth().basic("admin", "admin")
		.when()
		  .post("/api/vets/")
		.then()
			.body(equalTo(newVetAsJSON))
			.contentType(ContentType.JSON)
            .statusCode(Status.CREATED.getStatusCode());
    }

    @Test
    public void testCreateVetError() throws Exception {
    	Vet newVet = vets.get(0);
    	newVet.setId(null);
    	newVet.setFirstName(null);
    	ObjectMapper mapper = new ObjectMapper();
    	String newVetAsJSON = mapper.writeValueAsString(newVet);
    	given()
			.auth().basic("admin", "admin")
		.when()
		  .post("/api/vets/")
		.then()
				.body(equalTo(newVetAsJSON))
				.contentType(ContentType.JSON)
				.statusCode(Status.BAD_REQUEST.getStatusCode());
     }

    @Test
    public void testUpdateVetSuccess() throws Exception {
    	given(this.clinicService.findVetById(1)).willReturn(vets.get(0));
    	Vet newVet = vets.get(0);
    	newVet.setFirstName("James");
    	ObjectMapper mapper = new ObjectMapper();
    	String newVetAsJSON = mapper.writeValueAsString(newVet);
    	given()
			.auth().basic("admin", "admin")
		.when()
		  .put("/api/vets/1")
		.then()
			.body(equalTo(newVetAsJSON))
			.contentType(ContentType.JSON)
            .statusCode(Status.NO_CONTENT.getStatusCode());

    	given()
			.auth().basic("admin", "admin")
		.when()
		  .get("/api/vets/1")
		.then()
			   .contentType(ContentType.JSON)
			   .statusCode(Status.OK.getStatusCode())
            .body("id", equalTo(1))
            .body("firstName", equalTo("James"));

    }

    @Test
    public void testUpdateVetError() throws Exception {
    	Vet newVet = vets.get(0);
    	newVet.setFirstName("");
    	ObjectMapper mapper = new ObjectMapper();
    	String newVetAsJSON = mapper.writeValueAsString(newVet);
    	given()
			.auth().basic("admin", "admin")
		.when()
		  .put("/api/vets/1")
		.then()
			.body(equalTo(newVetAsJSON))
			.contentType(ContentType.JSON)
            .statusCode(Status.BAD_REQUEST.getStatusCode());
     }

    @Test
    public void testDeleteVetSuccess() throws Exception {
    	Vet newVet = vets.get(0);
    	ObjectMapper mapper = new ObjectMapper();
    	String newVetAsJSON = mapper.writeValueAsString(newVet);
    	given(this.clinicService.findVetById(1)).willReturn(vets.get(0));
    	given()
			.auth().basic("admin", "admin")
		.when()
		  .delete("/api/vets/1")
		.then()
			.body(equalTo(newVetAsJSON))
			.contentType(ContentType.JSON)
            .statusCode(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void testDeleteVetError() throws Exception {
    	Vet newVet = vets.get(0);
    	ObjectMapper mapper = new ObjectMapper();
    	String newVetAsJSON = mapper.writeValueAsString(newVet);
    	given(this.clinicService.findVetById(-1)).willReturn(null);
    	given()
			.auth().basic("admin", "admin")
		.when()
		  .delete("/api/vets/-1")
		.then()
			.body(equalTo(newVetAsJSON))
			.contentType(ContentType.JSON)
            .statusCode(Status.NOT_FOUND.getStatusCode());
    }

}

