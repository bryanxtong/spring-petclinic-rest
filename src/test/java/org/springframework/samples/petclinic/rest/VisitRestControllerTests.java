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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.service.ClinicService;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;

/**
 * Test class for {@link VisitRestController}
 *
 * @author Vitaliy Fedoriv
 */
@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class VisitRestControllerTests {
    @InjectMock
    private ClinicService clinicService;

    private List<Visit> visits;

    @BeforeEach
    public void initVisits(){
    	visits = new ArrayList<Visit>();

    	Owner owner = new Owner();
    	owner.setId(1);
    	owner.setFirstName("Eduardo");
    	owner.setLastName("Rodriquez");
    	owner.setAddress("2693 Commerce St.");
    	owner.setCity("McFarland");
    	owner.setTelephone("6085558763");

    	PetType petType = new PetType();
    	petType.setId(2);
    	petType.setName("dog");

    	Pet pet = new Pet();
    	pet.setId(8);
    	pet.setName("Rosy");
    	pet.setBirthDate(new Date());
    	pet.setOwner(owner);
    	pet.setType(petType);


    	Visit visit = new Visit();
    	visit.setId(2);
    	visit.setPet(pet);
    	visit.setDate(new Date());
    	visit.setDescription("rabies shot");
    	visits.add(visit);

    	visit = new Visit();
    	visit.setId(3);
    	visit.setPet(pet);
    	visit.setDate(new Date());
    	visit.setDescription("neutered");
    	visits.add(visit);


    }

    @Test
    public void testGetVisitSuccess() throws Exception {
    	given(this.clinicService.findVisitById(2)).willReturn(visits.get(0));
        given()
			.auth().basic("owner_admin", "admin")
		.when()
		  .get("/api/visits/2")
		.then()
			.contentType(ContentType.JSON)
			.statusCode(Status.OK.getStatusCode())
            .body("id", equalTo(2))
            .body("description", equalTo("rabies shot"));
    }

    @Test
    public void testGetVisitNotFound() throws Exception {
    	given(this.clinicService.findVisitById(-1)).willReturn(null);
        given()
			.auth().basic("owner_admin", "admin")
		.when()
		  .get("/api/visits/-1")
		.then()
			.contentType(ContentType.JSON)
			.statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testGetAllVisitsSuccess() throws Exception {
    	given(this.clinicService.findAllVisits()).willReturn(visits);
        given()
			.auth().basic("owner_admin", "admin")
		.when()
		  .get("/api/visits/")
		.then()
        	.contentType(ContentType.JSON)
			.statusCode(Status.OK.getStatusCode())
        	.body("[0].id", equalTo(2))
        	.body("[0].description", equalTo("rabies shot"))
        	.body("[1].id", equalTo(3))
        	.body("[1].description", equalTo("neutered"));
    }

    @Test
    public void testGetAllVisitsNotFound() throws Exception {
    	visits.clear();
    	given(this.clinicService.findAllVisits()).willReturn(visits);
        given()
			.auth().basic("owner_admin", "admin")
		.when()
		  .get("/api/visits/")
		.then()
			.contentType(ContentType.JSON)
			.statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testCreateVisitSuccess() throws Exception {
    	Visit newVisit = visits.get(0);
    	newVisit.setId(999);
    	ObjectMapper mapper = new ObjectMapper();
    	String newVisitAsJSON = mapper.writeValueAsString(newVisit);
    	System.out.println("newVisitAsJSON " + newVisitAsJSON);
    	given()
			.auth().basic("owner_admin", "admin")
		.when()
		  .post("/api/visits/")
		.then()
			.body(equalTo(newVisitAsJSON))
			.statusCode(Status.CREATED.getStatusCode())
			.contentType(ContentType.JSON);
    }

    @Test
    public void testCreateVisitError() throws Exception {
		assertThrows(IOException.class, () -> {
			Visit newVisit = visits.get(0);
			newVisit.setId(null);
			newVisit.setPet(null);
			ObjectMapper mapper = new ObjectMapper();
			String newVisitAsJSON = mapper.writeValueAsString(newVisit);
			given()
				.auth().basic("owner_admin", "admin")
			.when()
			.post("/api/visits/")
			.then()
				.body(equalTo(newVisitAsJSON))
				.statusCode(Status.BAD_REQUEST.getStatusCode())
				.contentType(ContentType.JSON);
		});
     }

    @Test
    public void testUpdateVisitSuccess() throws Exception {
    	given(this.clinicService.findVisitById(2)).willReturn(visits.get(0));
    	Visit newVisit = visits.get(0);
    	newVisit.setDescription("rabies shot test");
    	ObjectMapper mapper = new ObjectMapper();
    	String newVisitAsJSON = mapper.writeValueAsString(newVisit);
    	given()
			.auth().basic("owner_admin", "admin")
		.when()
		  .put("/api/visits/2")
		.then()
			.body(equalTo(newVisitAsJSON))
			.statusCode(Status.NO_CONTENT.getStatusCode())
			.contentType(ContentType.JSON);

    	given()
			.auth().basic("owner_admin", "admin")
		.when()
		  .get("/api/visits/2")
		.then()
			.contentType(ContentType.JSON)
			.statusCode(Status.OK.getStatusCode())
            .body("id", equalTo(2))
            .body("description", equalTo("rabies shot test"));
    }

    @Test
    public void testUpdateVisitError() throws Exception {
		assertThrows(IOException.class, () -> {
			Visit newVisit = visits.get(0);
			newVisit.setPet(null);
			ObjectMapper mapper = new ObjectMapper();
			String newVisitAsJSON = mapper.writeValueAsString(newVisit);
			given()
				.auth().basic("owner_admin", "admin")
			.when()
			.put("/api/visits/2")
			.then()
				.body(equalTo(newVisitAsJSON))
				.statusCode(Status.BAD_REQUEST.getStatusCode())

				.contentType(ContentType.JSON);
		} );
     }

    @Test
    public void testDeleteVisitSuccess() throws Exception {
    	Visit newVisit = visits.get(0);
    	ObjectMapper mapper = new ObjectMapper();
    	String newVisitAsJSON = mapper.writeValueAsString(newVisit);
    	given(this.clinicService.findVisitById(2)).willReturn(visits.get(0));
    	given()
			.auth().basic("owner_admin", "admin")
		.when()
		  .delete("/api/visits/2")
		.then()
			.body(equalTo(newVisitAsJSON))
			.statusCode(Status.NO_CONTENT.getStatusCode())
			.contentType(ContentType.JSON);

    }

    @Test
    public void testDeleteVisitError() throws Exception {
    	Visit newVisit = visits.get(0);
    	ObjectMapper mapper = new ObjectMapper();
    	String newVisitAsJSON = mapper.writeValueAsString(newVisit);
    	given(this.clinicService.findVisitById(-1)).willReturn(null);
    	given()
			.auth().basic("owner_admin", "admin")
		.when()
		  .delete("/api/visits/-1")
		.then()
			.body(equalTo(newVisitAsJSON))
			.statusCode(Status.NOT_FOUND.getStatusCode())
			.contentType(ContentType.JSON);

    }

}
