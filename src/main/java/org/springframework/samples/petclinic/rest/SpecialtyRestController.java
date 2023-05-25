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

import java.util.ArrayList;
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
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.security.Roles;
import org.springframework.samples.petclinic.service.ClinicService;

/**
 * @author Vitaliy Fedoriv
 *
 */

@Path("api/specialties")
public class SpecialtyRestController {

	@Inject
	ClinicService clinicService;

	@RolesAllowed(Roles.VET_ADMIN)
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllSpecialtys(){
		Collection<Specialty> specialties = new ArrayList<Specialty>();
		specialties.addAll(this.clinicService.findAllSpecialties());
		if (specialties.isEmpty()){
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(specialties).build();
	}

	@RolesAllowed(Roles.VET_ADMIN)
	@GET
	@Path("/{specialtyId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSpecialty(@PathParam("specialtyId") int specialtyId){
		Specialty specialty = this.clinicService.findSpecialtyById(specialtyId);
		if(specialty == null){
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(specialty).build();
	}

	@RolesAllowed(Roles.VET_ADMIN)
	@POST
	@Path("")
	@Produces( MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Response addSpecialty( @Valid Specialty specialty) {
		this.clinicService.saveSpecialty(specialty);
		return Response.status(Status.CREATED).entity(specialty).build();
	}

	@RolesAllowed(Roles.VET_ADMIN)
	@PUT
	@Path("/{specialtyId}")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Response updateSpecialty(@PathParam("specialtyId") int specialtyId,@Valid Specialty specialty) {
		Specialty currentSpecialty = this.clinicService.findSpecialtyById(specialtyId);
		if(currentSpecialty == null){
			return Response.status(Status.NOT_FOUND).build();
		}
		currentSpecialty.setName(specialty.getName());
		this.clinicService.saveSpecialty(currentSpecialty);
		return Response.noContent().entity(currentSpecialty).build();
	}

	@RolesAllowed(Roles.VET_ADMIN)
	@DELETE
	@Path("/{specialtyId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response deleteSpecialty(@PathParam("specialtyId") int specialtyId){
		Specialty specialty = this.clinicService.findSpecialtyById(specialtyId);
		if(specialty == null){
			return Response.status(Status.NOT_FOUND).build();
		}
		this.clinicService.deleteSpecialty(specialty);
		return Response.noContent().build();
	}

}
