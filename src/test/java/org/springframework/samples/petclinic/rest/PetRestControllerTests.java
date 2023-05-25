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
import java.util.Date;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.ClinicService;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;


/**
 * Test class for {@link PetRestController}
 *
 * @author Vitaliy Fedoriv
 */

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)

public class PetRestControllerTests {

    @Inject
    PetRestController petRestController;

    @InjectMock
    protected ClinicService clinicService;

    private List<Pet> pets;

    @BeforeEach
    public void initPets(){
    	pets = new ArrayList<Pet>();

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
    	pet.setId(3);
    	pet.setName("Rosy");
    	pet.setBirthDate(new Date());
    	pet.setOwner(owner);
    	pet.setType(petType);
    	pets.add(pet);

    	pet = new Pet();
    	pet.setId(4);
    	pet.setName("Jewel");
    	pet.setBirthDate(new Date());
    	pet.setOwner(owner);
    	pet.setType(petType);
    	pets.add(pet);
    }

    @Test
    public void testGetPetSuccess() throws Exception {
    	given(this.clinicService.findPetById(3)).willReturn(pets.get(0));
		given()
			.auth().basic("admin", "admin")
		.when()
		  .get("/api/pets/3")
		.then()
			//.contentType(ContentType.JSON)
			.statusCode(Status.OK.getStatusCode())
            .body("id", equalTo(3))
            .body("name", equalTo("Rosy"));
    }

    @Test
    public void testGetPetNotFound() throws Exception {
    	given(this.clinicService.findPetById(-1)).willReturn(null);
        given()
			.auth().basic("admin", "admin")
		.when()
			.get("/api/pets/-1")
		.then()
			.contentType(ContentType.JSON)
            .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testGetAllPetsSuccess() throws Exception {
    	given(this.clinicService.findAllPets()).willReturn(pets);
        given()
			.auth().basic("admin", "admin")
		.when()
			.get("/api/pets/")
		.then()
			.contentType(ContentType.JSON)
            .statusCode(Status.OK.getStatusCode())
            .body("[0].id", equalTo(3))
            .body("[0].name", equalTo("Rosy"))
            .body("[1].id", equalTo(4))
            .body("[1].name", equalTo("Jewel"));
    }

    @Test
    public void testGetAllPetsNotFound() throws Exception {
    	pets.clear();
    	given(this.clinicService.findAllPets()).willReturn(pets);
        given()
			.auth().basic("admin", "admin")
		.when()
			.get("/api/pets/")
		.then()
			.contentType(ContentType.JSON)
            .statusCode(Status.OK.getStatusCode());
    }

    @Test
    public void testCreatePetSuccess() throws Exception {
    	Pet newPet = pets.get(0);
    	newPet.setId(999);
    	ObjectMapper mapper = new ObjectMapper();
    	String newPetAsJSON = mapper.writeValueAsString(newPet);
    	given()
			.auth().basic("admin", "admin")
			.contentType(ContentType.JSON)
			.body(newPetAsJSON)
		.when()
			.post("/api/pets/")
		.then()
			.body(equalTo(newPetAsJSON))
			.contentType(ContentType.JSON)
    		.statusCode(Status.CREATED.getStatusCode());
    }

    @Test
    public void testCreatePetError() throws Exception {
    	Pet newPet = pets.get(0);
    	newPet.setId(null);
    	newPet.setName(null);
    	ObjectMapper mapper = new ObjectMapper();
    	String newPetAsJSON = mapper.writeValueAsString(newPet);
    	given()
			.auth().basic("admin", "admin")
			.contentType(ContentType.JSON)
			.body(newPetAsJSON)
		.when()
			.post("/api/pets/")
		.then()
			.body(equalTo(newPetAsJSON))
			.contentType(ContentType.JSON)
		    .statusCode(Status.BAD_REQUEST.getStatusCode());
     }

    @Test
    public void testUpdatePetSuccess() throws Exception {
    	given(this.clinicService.findPetById(3)).willReturn(pets.get(0));
    	Pet newPet = pets.get(0);
    	newPet.setName("Rosy I");
    	ObjectMapper mapper = new ObjectMapper();
    	String newPetAsJSON = mapper.writeValueAsString(newPet);
    	given()
			.auth().basic("admin", "admin")
			.contentType(ContentType.JSON)
			.body(newPetAsJSON)
		.when()
			.put("/api/pets/3")
		.then()
			.body(equalTo(newPetAsJSON))
			.contentType(ContentType.JSON)
        	.statusCode(Status.NO_CONTENT.getStatusCode());

    	given()
			.auth().basic("admin", "admin")
		.when()
			.get("/api/pets/3")
		.then()
			.contentType(ContentType.JSON)
			.statusCode(Status.OK.getStatusCode())
            .body("id", equalTo(3))
            .body("name", equalTo("Rosy I"));

    }

    @Test
    public void testUpdatePetError() throws Exception {
    	Pet newPet = pets.get(0);
    	newPet.setName("");
    	ObjectMapper mapper = new ObjectMapper();
    	String newPetAsJSON = mapper.writeValueAsString(newPet);
    	given()
			.auth().basic("admin", "admin")
			.contentType(ContentType.JSON)
			.body(newPetAsJSON)
		.when()
			.put("/api/pets/3")
		.then()
			.body(equalTo(newPetAsJSON))
			.contentType(ContentType.JSON)
        	.statusCode(Status.BAD_REQUEST.getStatusCode());
     }

    @Test
    public void testDeletePetSuccess() throws Exception {
    	Pet newPet = pets.get(0);
    	ObjectMapper mapper = new ObjectMapper();
    	String newPetAsJSON = mapper.writeValueAsString(newPet);
    	given(this.clinicService.findPetById(3)).willReturn(pets.get(0));
    	given()
			.auth().basic("admin", "admin")
		.when()
			.delete("/api/pets/3")
		.then()
			.body(equalTo(newPetAsJSON))
			.contentType(ContentType.JSON)
        	.statusCode(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    public void testDeletePetError() throws Exception {
    	Pet newPet = pets.get(0);
    	ObjectMapper mapper = new ObjectMapper();
    	String newPetAsJSON = mapper.writeValueAsString(newPet);
    	given(this.clinicService.findPetById(-1)).willReturn(null);
    	given()
			.auth().basic("admin", "admin")
		.when()
			.delete("/api/pets/-1")
		.then()
			.body(equalTo(newPetAsJSON))
			.contentType(ContentType.JSON)
        	.statusCode(Status.NOT_FOUND.getStatusCode());
    }

}
