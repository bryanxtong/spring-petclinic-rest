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

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.ClinicService;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;


/**
 * Test class for {@link PetTypeRestController}
 *
 * @author Vitaliy Fedoriv
 */
@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)

 public class PetTypeRestControllerTests {

    @Inject
    PetTypeRestController petTypeRestController;

    @InjectMock
    private ClinicService clinicService;

    private List<PetType> petTypes;

    @BeforeEach
    public void initPetTypes(){
    	petTypes = new ArrayList<PetType>();

    	PetType petType = new PetType();
    	petType.setId(1);
    	petType.setName("cat");
    	petTypes.add(petType);

    	petType = new PetType();
    	petType.setId(2);
    	petType.setName("dog");
    	petTypes.add(petType);

    	petType = new PetType();
    	petType.setId(3);
    	petType.setName("lizard");
    	petTypes.add(petType);

    	petType = new PetType();
    	petType.setId(4);
    	petType.setName("snake");
    	petTypes.add(petType);
    }

    @Test
    public void testGetPetTypeSuccessAsOwnerAdmin() throws Exception {
    	given(this.clinicService.findPetTypeById(1)).willReturn(petTypes.get(0));
        given()
			.auth().basic("admin", "admin")
		.when()
          .get("/api/pettypes/1")
        .then()
        .contentType(ContentType.JSON)
        .statusCode(Status.OK.getStatusCode())
            .body("id", equalTo(1))
            .body("name", equalTo("cat"));
    }

    @Test
    public void testGetPetTypeSuccessAsVetAdmin() throws Exception {
        given(this.clinicService.findPetTypeById(1)).willReturn(petTypes.get(0));
        given()
			.auth().basic("admin", "admin")
		.when()
          .get("/api/pettypes/1")
        .then()
          .contentType(ContentType.JSON)
          .statusCode(Status.OK.getStatusCode())
            .body("id", equalTo(1))
            .body("name", equalTo("cat"));
    }

    @Test
    public void testGetPetTypeNotFound() throws Exception {
    	given(this.clinicService.findPetTypeById(-1)).willReturn(null);
        given()
			.auth().basic("admin", "admin")
		.when()
          .get("/api/pettypes/-1")
        .then()
          .contentType(ContentType.JSON)
          .statusCode(Status.NOT_FOUND.getStatusCode());
  }

    @Test
    public void testGetAllPetTypesSuccessAsOwnerAdmin() throws Exception {
    	petTypes.remove(0);
    	petTypes.remove(1);
    	given(this.clinicService.findAllPetTypes()).willReturn(petTypes);
        given()
			.auth().basic("admin", "admin")
		.when()
          .get("/api/pettypes/")
        .then()
          .contentType(ContentType.JSON)
          .statusCode(Status.OK.getStatusCode())
        	.body("[0].id", equalTo(2))
        	.body("[0].name", equalTo("dog"))
        	.body("[1].id", equalTo(4))
        	.body("[1].name", equalTo("snake"));
    }

    @Test
    public void testGetAllPetTypesSuccessAsVetAdmin() throws Exception {
        petTypes.remove(0);
        petTypes.remove(1);
        given(this.clinicService.findAllPetTypes()).willReturn(petTypes);
        given()
			.auth().basic("admin", "admin")
		.when()
          .get("/api/pettypes/")
        .then()
          .contentType(ContentType.JSON)
          .statusCode(Status.OK.getStatusCode())
            .body("[0].id", equalTo(2))
            .body("[0].name", equalTo("dog"))
            .body("[1].id", equalTo(4))
            .body("[1].name", equalTo("snake"));
    }

    @Test
    public void testGetAllPetTypesNotFound() throws Exception {
    	petTypes.clear();
    	given(this.clinicService.findAllPetTypes()).willReturn(petTypes);
        given()
			.auth().basic("admin", "admin")
		.when()
          .get("/api/pettypes/")
        .then()
          .contentType(ContentType.JSON)
          .statusCode(Status.NOT_FOUND.getStatusCode());
  }

    @Test
    public void testCreatePetTypeSuccess() throws Exception {
    	PetType newPetType = petTypes.get(0);
    	newPetType.setId(999);
    	ObjectMapper mapper = new ObjectMapper();
    	String newPetTypeAsJSON = mapper.writeValueAsString(newPetType);
    	given()
			.auth().basic("admin", "admin")
		.when()
          .post("/api/pettypes/")
        .then()
            .body(equalTo(newPetTypeAsJSON))
			.contentType(ContentType.JSON)
			.statusCode(Status.CREATED.getStatusCode());

    }

    @Test
    public void testCreatePetTypeError() throws Exception {
    	PetType newPetType = petTypes.get(0);
    	newPetType.setId(null);
    	newPetType.setName(null);
    	ObjectMapper mapper = new ObjectMapper();
    	String newPetTypeAsJSON = mapper.writeValueAsString(newPetType);
    	given()
			.auth().basic("admin", "admin")
		.when()
          .post("/api/pettypes/")
        .then()
                .body(equalTo(newPetTypeAsJSON))
                .contentType(ContentType.JSON)
                .statusCode(Status.BAD_REQUEST.getStatusCode());

     }

    @Test
    public void testUpdatePetTypeSuccess() throws Exception {
    	given(this.clinicService.findPetTypeById(2)).willReturn(petTypes.get(1));
    	PetType newPetType = petTypes.get(1);
    	newPetType.setName("dog I");
    	ObjectMapper mapper = new ObjectMapper();
    	String newPetTypeAsJSON = mapper.writeValueAsString(newPetType);
    	given()
			.auth().basic("admin", "admin")
		.when()
          .put("/api/pettypes/2")
        .then()
            .body(equalTo(newPetTypeAsJSON))
			.contentType(ContentType.JSON)
			.statusCode(Status.NO_CONTENT.getStatusCode());


    	given()
			.auth().basic("admin", "admin")
		.when()
          .get("/api/pettypes/2")
        .then()
          .contentType(ContentType.JSON)
          .statusCode(Status.OK.getStatusCode())
            .body("id", equalTo(2))
            .body("name", equalTo("dog I"));
    }

    @Test
    public void testUpdatePetTypeError() throws Exception {
    	PetType newPetType = petTypes.get(0);
    	newPetType.setName("");
    	ObjectMapper mapper = new ObjectMapper();
    	String newPetTypeAsJSON = mapper.writeValueAsString(newPetType);
    	given()
			.auth().basic("admin", "admin")
		.when()
          .put("/api/pettypes/1")
        .then()
            .body(equalTo(newPetTypeAsJSON))
			.contentType(ContentType.JSON)
			.statusCode(Status.BAD_REQUEST.getStatusCode());

     }

    @Test
    public void testDeletePetTypeSuccess() throws Exception {
    	PetType newPetType = petTypes.get(0);
    	ObjectMapper mapper = new ObjectMapper();
    	String newPetTypeAsJSON = mapper.writeValueAsString(newPetType);
    	given(this.clinicService.findPetTypeById(1)).willReturn(petTypes.get(0));
    	given()
			.auth().basic("admin", "admin")
		.when()
          .delete("/api/pettypes/1")
        .then()
            .body(equalTo(newPetTypeAsJSON))
			.contentType(ContentType.JSON)
			.statusCode(Status.NO_CONTENT.getStatusCode());

    }

    @Test
    public void testDeletePetTypeError() throws Exception {
    	PetType newPetType = petTypes.get(0);
    	ObjectMapper mapper = new ObjectMapper();
    	String newPetTypeAsJSON = mapper.writeValueAsString(newPetType);
    	given(this.clinicService.findPetTypeById(-1)).willReturn(null);
    	given()
			.auth().basic("admin", "admin")
		.when()
          .delete("/api/pettypes/-1")
        .then()
            .body(equalTo(newPetTypeAsJSON))
          .contentType(ContentType.JSON)
          .statusCode(Status.NOT_FOUND.getStatusCode());

    }

}
