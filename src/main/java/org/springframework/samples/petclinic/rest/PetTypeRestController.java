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

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.security.Roles;
import org.springframework.samples.petclinic.service.ClinicService;

@Path("api/pettypes")
public class PetTypeRestController {

	@Inject
	ClinicService clinicService;

	//@RolesAllowed( {Roles.OWNER_ADMIN, Roles.VET_ADMIN })
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getAllPetTypes(){
        return this.clinicService.findAllPetTypes().onItem().ifNotNull().transform(petTypes -> Response.ok(petTypes).build())
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

	//@RolesAllowed( {Roles.OWNER_ADMIN, Roles.VET_ADMIN })
	@GET
	@Path("/{petTypeId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getPetType(@PathParam("petTypeId") int petTypeId){
        return this.clinicService.findPetTypeById(petTypeId).onItem().ifNotNull()
            .transformToUni(petType -> Uni.createFrom().item(Response.ok(petType).build()))
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

	//@RolesAllowed(Roles.VET_ADMIN)
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Uni<Response> addPetType(@Valid PetType petType) {
        return this.clinicService.savePetType(petType).replaceWith(Response.status(Status.CREATED).entity(petType).build());
	}

	//@RolesAllowed(Roles.VET_ADMIN)
	@PUT
	@Path("/{petTypeId}")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Uni<Response> updatePetType(@PathParam("petTypeId") int petTypeId, @Valid PetType petType) {
        return this.clinicService.findPetTypeById(petTypeId)
            .onItem().ifNotNull().transformToUni(currentPetType -> {
            currentPetType.setName(petType.getName());
            return clinicService.savePetType(currentPetType)
                .onItem().transform(en->Response.noContent().entity(currentPetType).build());
        }).onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

	//@RolesAllowed(Roles.VET_ADMIN)
	@DELETE
	@Path("/{petTypeId}")
	@Produces(MediaType.APPLICATION_JSON)
	@WithTransaction
	public Uni<Response> deletePetType(@PathParam("petTypeId") int petTypeId){
        return this.clinicService.findPetTypeById(petTypeId)
            .onItem().ifNotNull().transformToUni(currentPetType -> clinicService.deletePetType(currentPetType).onItem().transform(en-> Response.noContent().build()))
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

}
