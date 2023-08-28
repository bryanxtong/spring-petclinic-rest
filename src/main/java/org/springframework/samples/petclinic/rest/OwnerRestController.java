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

import java.util.Set;
import java.util.stream.Collectors;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
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
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.security.Roles;
import org.springframework.samples.petclinic.service.ClinicService;

/**
 * @author Vitaliy Fedoriv
 *
 */

@Path("/api/owners")
public class OwnerRestController {

	@Inject
	ClinicService clinicService;

	@Inject
	Validator validator;

	//@RolesAllowed( Roles.OWNER_ADMIN)
	@GET
	@Path("/*/lastname/{lastName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getOwnersList(@PathParam("lastName") String ownerLastName) {
		if (ownerLastName == null) {
            ownerLastName = "";
        }
        return this.clinicService.findOwnerByLastName(ownerLastName)
            .onItem().ifNotNull().transform(owners -> Response.ok(owners).status(Status.OK).build())
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());

	}

	//@RolesAllowed( Roles.OWNER_ADMIN )
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getOwners() {
        return this.clinicService.findAllOwners()
            .onItem().ifNotNull().transform(owners -> Response.ok(owners).status(Status.OK).build())
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

	//@RolesAllowed( Roles.OWNER_ADMIN)
	@GET
	@Path("/{ownerId}")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Uni<Response> getOwner(@PathParam("ownerId") int ownerId) {
        return this.clinicService.findOwnerById(ownerId)
            .onItem().ifNotNull().transform(owner -> Response.ok(owner).status(Status.OK).build())
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

	//@RolesAllowed( Roles.OWNER_ADMIN )
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
    //@Consumes(MediaType.APPLICATION_JSON)
	public Uni<Response> addOwner( Owner owner) {
		Set<ConstraintViolation<Owner>> errors = validator.validate(owner);
		if (!errors.isEmpty() || (owner == null)) {
			return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).header("errors", errors.stream().collect(Collectors.toMap(ConstraintViolation::getPropertyPath, ConstraintViolation::getMessage))).entity(owner).build());
		}
        return this.clinicService.saveOwner(owner).replaceWith(Response.ok(owner).status(Status.CREATED).build());

	}

	//@RolesAllowed( Roles.OWNER_ADMIN )
	@PUT
	@Path("/{ownerId}")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Uni<Response> updateOwner(@PathParam("ownerId") int ownerId, @Valid Owner owner) { // ,BindingResult bindingResult, UriComponentsBuilder ucBuilder) {
        Set<ConstraintViolation<Owner>> errors = validator.validate(owner);
		if (!errors.isEmpty() || (owner == null)) {
			return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).entity(owner).header("errors", errors.stream().collect(Collectors.toMap(ConstraintViolation::getPropertyPath, ConstraintViolation::getMessage))).build());
		}

        return this.clinicService.findOwnerById(ownerId).onItem().ifNotNull().transformToUni(currentOwner -> {
            currentOwner.setAddress(owner.getAddress());
            currentOwner.setCity(owner.getCity());
            currentOwner.setFirstName(owner.getFirstName());
            currentOwner.setLastName(owner.getLastName());
            currentOwner.setTelephone(owner.getTelephone());
            return clinicService.saveOwner(currentOwner).onItem().transform(en-> Response.status(Status.NO_CONTENT).build());
        }).onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());

	}

	//@RolesAllowed( Roles.OWNER_ADMIN )
	@DELETE
	@Path("/{ownerId}")
	@Produces(MediaType.APPLICATION_JSON)
	@WithTransaction
	public Uni<Response> deleteOwner(@PathParam("ownerId") int ownerId) {
        return this.clinicService.findOwnerById(ownerId)
            .onItem().ifNotNull().transformToUni(owner -> clinicService.deleteOwner(owner).onItem().transform(en->Response.status(Status.NO_CONTENT).build()))
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());

	}

}
