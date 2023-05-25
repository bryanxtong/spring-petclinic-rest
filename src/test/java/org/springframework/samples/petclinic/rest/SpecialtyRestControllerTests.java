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
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.service.ClinicService;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;

/**
 * Test class for {@link SpecialtyRestController}
 *
 * @author Vitaliy Fedoriv
 */
@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)

public class SpecialtyRestControllerTests {

	@InjectMock
    private ClinicService clinicService;

    private List<Specialty> specialties;

    @BeforeEach
    public void initSpecialtys(){
    	specialties = new ArrayList<Specialty>();

    	Specialty specialty = new Specialty();
    	specialty.setId(1);
    	specialty.setName("radiology");
    	specialties.add(specialty);

    	specialty = new Specialty();
    	specialty.setId(2);
    	specialty.setName("surgery");
    	specialties.add(specialty);

    	specialty = new Specialty();
    	specialty.setId(3);
    	specialty.setName("dentistry");
    	specialties.add(specialty);

    }

    @Test
    public void testGetSpecialtySuccess() throws Exception {
    	given(this.clinicService.findSpecialtyById(1)).willReturn(specialties.get(0));
        given()
			.auth().basic("admin", "admin")
		.when()
		  .get("/api/specialties/1")
		.then()
		  .contentType(ContentType.JSON)
		  .statusCode(Status.OK.getStatusCode())

            .body("id", equalTo(1))
            .body("$.name", equalTo("radiology"));
    }

    @Test
    public void testGetSpecialtyNotFound() throws Exception {
    	given(this.clinicService.findSpecialtyById(-1)).willReturn(null);
        given()
			.auth().basic("admin", "admin")
		.when()
		  .get("/api/specialties/-1")
		  .then()
			.contentType(ContentType.JSON)
			.statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testGetAllSpecialtysSuccess() throws Exception {
    	specialties.remove(0);
    	given(this.clinicService.findAllSpecialties()).willReturn(specialties);
        given()
			.auth().basic("admin", "admin")
		.when()
		  .get("/api/specialties/")
		.then()
			.contentType(ContentType.JSON)
			.statusCode(Status.OK.getStatusCode())

        	.body("[0].id", equalTo(2))
        	.body("[0].name", equalTo("surgery"))
        	.body("[1].id", equalTo(3))
        	.body("[1].name", equalTo("dentistry"));
    }

    @Test
    public void testGetAllSpecialtysNotFound() throws Exception {
    	specialties.clear();
    	given(this.clinicService.findAllSpecialties()).willReturn(specialties);
        given()
			.auth().basic("admin", "admin")
		.when()
		  .get("/api/specialties/")
		.then()
			.contentType(ContentType.JSON)
			.statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testCreateSpecialtySuccess() throws Exception {
    	Specialty newSpecialty = specialties.get(0);
    	newSpecialty.setId(999);
    	ObjectMapper mapper = new ObjectMapper();
    	String newSpecialtyAsJSON = mapper.writeValueAsString(newSpecialty);
    	given()
			.auth().basic("admin", "admin")
		.when()
		  .post("/api/specialties/")
		.then()
			.body(equalTo(newSpecialtyAsJSON))
			.contentType(ContentType.JSON)
			.statusCode(Status.CREATED.getStatusCode());
    }

    @Test
    public void testCreateSpecialtyError() throws Exception {
    	Specialty newSpecialty = specialties.get(0);
    	newSpecialty.setId(null);
    	newSpecialty.setName(null);
    	ObjectMapper mapper = new ObjectMapper();
    	String newSpecialtyAsJSON = mapper.writeValueAsString(newSpecialty);
    	given()
			.auth().basic("admin", "admin")
		.when()
		  .post("/api/specialties/")
		.then()
				.body(equalTo(newSpecialtyAsJSON))
				.contentType(ContentType.JSON)
				.statusCode(Status.BAD_REQUEST.getStatusCode());
     }

    @Test
    public void testUpdateSpecialtySuccess() throws Exception {
    	given(this.clinicService.findSpecialtyById(2)).willReturn(specialties.get(1));
    	Specialty newSpecialty = specialties.get(1);
    	newSpecialty.setName("surgery I");
    	ObjectMapper mapper = new ObjectMapper();
    	String newSpecialtyAsJSON = mapper.writeValueAsString(newSpecialty);
    	given()
			.auth().basic("admin", "admin")
		.when()
		  .put("/api/specialties/2")
		.then()
			.body(equalTo(newSpecialtyAsJSON))
			.contentType(ContentType.JSON)
			.statusCode(Status.NO_CONTENT.getStatusCode());

    	given()
			.auth().basic("admin", "admin")
		.when()
		  .get("/api/specialties/2")
		.then()
		  .contentType(ContentType.JSON)
		  .statusCode(Status.OK.getStatusCode())
            .body("id", equalTo(2))
            .body("name", equalTo("surgery I"));
    }

    @Test
    public void testUpdateSpecialtyError() throws Exception {
    	Specialty newSpecialty = specialties.get(0);
    	newSpecialty.setName("");
    	ObjectMapper mapper = new ObjectMapper();
    	String newSpecialtyAsJSON = mapper.writeValueAsString(newSpecialty);
    	given()
			.auth().basic("admin", "admin")
		.when()
		  .put("/api/specialties/1")
		.then()
		  .contentType(ContentType.JSON)
			.body(equalTo(newSpecialtyAsJSON))
			.statusCode(Status.BAD_REQUEST.getStatusCode());
     }

    @Test
    public void testDeleteSpecialtySuccess() throws Exception {
    	Specialty newSpecialty = specialties.get(0);
    	ObjectMapper mapper = new ObjectMapper();
    	String newSpecialtyAsJSON = mapper.writeValueAsString(newSpecialty);
    	given(this.clinicService.findSpecialtyById(1)).willReturn(specialties.get(0));
    	given()
			.auth().basic("admin", "admin")
		.when()
		  .delete("/api/specialties/1")
		.then()
			.body(equalTo(newSpecialtyAsJSON))
			.contentType(ContentType.JSON)
			.statusCode(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void testDeleteSpecialtyError() throws Exception {
    	Specialty newSpecialty = specialties.get(0);
    	ObjectMapper mapper = new ObjectMapper();
    	String newSpecialtyAsJSON = mapper.writeValueAsString(newSpecialty);
    	given(this.clinicService.findSpecialtyById(-1)).willReturn(null);
    	given()
			.auth().basic("admin", "admin")
		.when()
		  .delete("/api/specialties/-1")
		  .then()
			.body(equalTo(newSpecialtyAsJSON))
			.contentType(ContentType.JSON)
			.statusCode(Status.NOT_FOUND.getStatusCode());
    }
}
