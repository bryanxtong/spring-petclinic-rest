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

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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

	@RolesAllowed( Roles.OWNER_ADMIN)
	@GET
	@Path("/*/lastname/{lastName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOwnersList(@PathParam("lastName") String ownerLastName) {
		if (ownerLastName == null) {
			ownerLastName = "";
		}
		Collection<Owner> owners = this.clinicService.findOwnerByLastName(ownerLastName);
		if (owners.isEmpty()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(owners).status(Status.OK).build();
	}

	@RolesAllowed( Roles.OWNER_ADMIN )
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOwners() {
		Collection<Owner> owners = this.clinicService.findAllOwners();
		if (owners.isEmpty()) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(owners).status(Status.OK).build();
	}

	@RolesAllowed( Roles.OWNER_ADMIN)
	@GET
	@Path("/{ownerId}")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Response getOwner(@PathParam("ownerId") int ownerId) {
		Owner owner = null;
		owner = this.clinicService.findOwnerById(ownerId);
		if (owner == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(owner).status(Status.OK).build();
	}

	//@RolesAllowed( Roles.OWNER_ADMIN )
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
    //@Consumes(MediaType.APPLICATION_JSON)
	public Response addOwner( Owner owner) {
		Set<ConstraintViolation<Owner>> errors = validator.validate(owner);
		if (!errors.isEmpty() || (owner == null)) {
			return Response.status(Status.BAD_REQUEST).header("errors", errors.stream().collect(Collectors.toMap(ConstraintViolation::getPropertyPath, ConstraintViolation::getMessage))).entity(owner).build();
		}
		this.clinicService.saveOwner(owner);
		//headers.setLocation(ucBuilder.path("/api/owners/{id}").buildAndExpand(owner.getId()).toUri()); // TODO
		return Response.ok(owner).status(Status.CREATED).build();
	}

	@RolesAllowed( Roles.OWNER_ADMIN )
	@PUT
	@Path("/{ownerId}")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Response updateOwner(@PathParam("ownerId") int ownerId, @Valid Owner owner) { // ,BindingResult bindingResult, UriComponentsBuilder ucBuilder) {
		Set<ConstraintViolation<Owner>> errors = validator.validate(owner);
		if (!errors.isEmpty() || (owner == null)) {
			return Response.status(Status.BAD_REQUEST).entity(owner).header("errors", errors.stream().collect(Collectors.toMap(ConstraintViolation::getPropertyPath, ConstraintViolation::getMessage))).build();
		}
		Owner currentOwner = this.clinicService.findOwnerById(ownerId);
		if (currentOwner == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		currentOwner.setAddress(owner.getAddress());
		currentOwner.setCity(owner.getCity());
		currentOwner.setFirstName(owner.getFirstName());
		currentOwner.setLastName(owner.getLastName());
		currentOwner.setTelephone(owner.getTelephone());
		this.clinicService.saveOwner(currentOwner);
		return Response.status(Status.NO_CONTENT).build();
	}

	@RolesAllowed( Roles.OWNER_ADMIN )
	@DELETE
	@Path("/{ownerId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response deleteOwner(@PathParam("ownerId") int ownerId) {
		Owner owner = this.clinicService.findOwnerById(ownerId);
		if (owner == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		this.clinicService.deleteOwner(owner);
		return Response.status(Status.NO_CONTENT).build();
	}

}
