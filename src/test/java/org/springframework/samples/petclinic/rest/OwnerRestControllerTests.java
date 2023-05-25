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
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.ClinicService;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;



/**
 * Test class for {@link OwnerRestController}
 *
 * @author Vitaliy Fedoriv
 */
@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
public class OwnerRestControllerTests {

    @InjectMock
    private ClinicService clinicService;

    private List<Owner> owners;

    @BeforeEach
    public void initOwners(){
    	owners = new ArrayList<Owner>();

    	Owner owner = new Owner();
    	owner.setId(1);
    	owner.setFirstName("George");
    	owner.setLastName("Franklin");
    	owner.setAddress("110 W. Liberty St.");
    	owner.setCity("Madison");
    	owner.setTelephone("6085551023");
    	owners.add(owner);

    	owner = new Owner();
    	owner.setId(2);
    	owner.setFirstName("Betty");
    	owner.setLastName("Davis");
    	owner.setAddress("638 Cardinal Ave.");
    	owner.setCity("Sun Prairie");
    	owner.setTelephone("6085551749");
    	owners.add(owner);

    	owner = new Owner();
    	owner.setId(3);
    	owner.setFirstName("Eduardo");
    	owner.setLastName("Rodriquez");
    	owner.setAddress("2693 Commerce St.");
    	owner.setCity("McFarland");
    	owner.setTelephone("6085558763");
    	owners.add(owner);

    	owner = new Owner();
    	owner.setId(4);
    	owner.setFirstName("Harold");
    	owner.setLastName("Davis");
    	owner.setAddress("563 Friendly St.");
    	owner.setCity("Windsor");
    	owner.setTelephone("6085553198");
    	owners.add(owner);


    }

    @Test
    public void testGetOwnerSuccess() throws Exception {
    	given(this.clinicService.findOwnerById(1)).willReturn(owners.get(0));
		given()
		  .auth().basic("admin", "admin")
		.when()
		  .get("/api/owners/1")
		.then()
		  .statusCode(200)
		  .contentType(ContentType.JSON)
          .body("id", equalTo(1))
          .body("firstName", equalTo("George"));
    }

    @Test
    public void testGetOwnerNotFound() throws Exception {
    	given(this.clinicService.findOwnerById(-1)).willReturn(null);
        given()
		  .auth().basic("admin", "admin")
		.when()
		  .get("/api/owners/-1")
		.then()
			.contentType(ContentType.JSON)
			.statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testGetOwnersListSuccess() throws Exception {
    	owners.remove(0);
    	owners.remove(1);
    	given(this.clinicService.findOwnerByLastName("Davis")).willReturn(owners);
        given()
		  .auth().basic("admin", "admin")
		.when()
		  .get("/api/owners/*/lastname/Davis")
		.then()
			.contentType(ContentType.JSON)
            .statusCode(Status.OK.getStatusCode())
            .body("[0].id", equalTo(2))
            .body("[0].firstName", equalTo("Betty"))
            .body("[1].id", equalTo(4))
            .body("[1].firstName", equalTo("Harold"));
    }

    @Test
    public void testGetOwnersListNotFound() throws Exception {
    	owners.clear();
    	given(this.clinicService.findOwnerByLastName("0")).willReturn(owners);
        given()
		  .auth().basic("admin", "admin")
		.when()
		  .get("/api/owners/?lastName=0")
		.then()
		  .contentType(ContentType.JSON)
		  .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testGetAllOwnersSuccess() throws Exception {
    	owners.remove(0);
    	owners.remove(1);
    	given(this.clinicService.findAllOwners()).willReturn(owners);
        given()
		  .auth().basic("admin", "admin")
		.when()
			.get("/api/owners/")
		.then()
			.statusCode(Status.OK.getStatusCode())
			.contentType(ContentType.JSON)
			.body("[0].id", equalTo(2))
            .body("[0].firstName", equalTo("Betty"))
            .body("[1].id", equalTo(4))
            .body("[1].firstName", equalTo("Harold"));
    }

    @Test
    public void testGetAllOwnersNotFound() throws Exception {
    	owners.clear();
    	given(this.clinicService.findAllOwners()).willReturn(owners);
        given()
		  .auth().basic("admin", "admin")
		.when()
		.get("/api/owners/")
		.then()
        	.contentType(ContentType.JSON)
			.statusCode(Status.NOT_FOUND.getStatusCode());
		}

    @Test
    public void testCreateOwnerSuccess() throws Exception {
    	Owner newOwner = owners.get(0);
    	newOwner.setId(999);
    	ObjectMapper mapper = new ObjectMapper();
    	String newOwnerAsJSON = mapper.writeValueAsString(newOwner);
    	given()
		  .auth().basic("admin", "admin")
		.when()
		  .post("/api/owners/")
		.then()
			.body(equalTo(newOwnerAsJSON))
        	.contentType(ContentType.JSON)
			.statusCode(Status.CREATED.getStatusCode());
		}

    @Test
    public void testCreateOwnerError() throws Exception {
    	Owner newOwner = owners.get(0);
    	newOwner.setId(null);
    	newOwner.setFirstName(null);
    	ObjectMapper mapper = new ObjectMapper();
    	String newOwnerAsJSON = mapper.writeValueAsString(newOwner);
    	given()
		  .auth().basic("admin", "admin")
		.when()
		  .post("/api/owners/")
		.then()
				.body(equalTo(newOwnerAsJSON))
				.contentType(ContentType.JSON)
				.statusCode(Status.BAD_REQUEST.getStatusCode());
			}

    @Test
    public void testUpdateOwnerSuccess() throws Exception {
    	given(this.clinicService.findOwnerById(1)).willReturn(owners.get(0));
    	Owner newOwner = owners.get(0);
    	newOwner.setFirstName("George I");
    	ObjectMapper mapper = new ObjectMapper();
    	String newOwnerAsJSON = mapper.writeValueAsString(newOwner);
    	given()
		  .auth().basic("admin", "admin")
		.when()
		  .put("/api/owners/1")
		.then()
			.body(equalTo(newOwnerAsJSON))
        	.contentType(ContentType.JSON)
			.statusCode(Status.NO_CONTENT.getStatusCode());

    	given()
		  .auth().basic("admin", "admin")
		.when()
		  .get("/api/owners/1")
		.then()
        	.contentType(ContentType.JSON)
			.statusCode(Status.OK.getStatusCode())
            .body("id", equalTo(1))
            .body("firstName", equalTo("George I"));

    }

    @Test
    public void testUpdateOwnerError() throws Exception {
    	Owner newOwner = owners.get(0);
    	newOwner.setFirstName("");
    	ObjectMapper mapper = new ObjectMapper();
    	String newOwnerAsJSON = mapper.writeValueAsString(newOwner);
    	given()
		  .auth().basic("admin", "admin")
		.when()
		  .put("/api/owners/1")
		.then()
			.body(equalTo(newOwnerAsJSON))
        	.contentType(ContentType.JSON)
			.statusCode(Status.BAD_REQUEST.getStatusCode());
		}

    @Test
    public void testDeleteOwnerSuccess() throws Exception {
    	Owner newOwner = owners.get(0);
    	ObjectMapper mapper = new ObjectMapper();
    	String newOwnerAsJSON = mapper.writeValueAsString(newOwner);
    	given(this.clinicService.findOwnerById(1)).willReturn(owners.get(0));
    	given()
		  .auth().basic("admin", "admin")
		.when()
		  .delete("/api/owners/1")
		.then()
			.body(equalTo(newOwnerAsJSON))
        	.contentType(ContentType.JSON)
			.statusCode(Status.NO_CONTENT.getStatusCode());
		}

    @Test
    public void testDeleteOwnerError() throws Exception {
    	Owner newOwner = owners.get(0);
    	ObjectMapper mapper = new ObjectMapper();
    	String newOwnerAsJSON = mapper.writeValueAsString(newOwner);
    	given(this.clinicService.findOwnerById(-1)).willReturn(null);
    	given()
		  .auth().basic("admin", "admin")
		.when()
		  .delete("/api/owners/-1")
		.then()
			.body(equalTo(newOwnerAsJSON))
        	.contentType(ContentType.JSON)
			.statusCode(Status.NOT_FOUND.getStatusCode());
		}

}
