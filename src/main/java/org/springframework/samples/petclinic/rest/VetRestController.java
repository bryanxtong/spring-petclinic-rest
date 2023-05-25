/*
 * Copyright 2016-2018 the original author or authors.
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
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.security.Roles;
import org.springframework.samples.petclinic.service.ClinicService;

/**
 * @author Vitaliy Fedoriv
 *
 */

@Path("api/vets")
public class VetRestController {

	@Inject
	ClinicService clinicService;

	@RolesAllowed(Roles.VET_ADMIN)
	@GET
	@Path("/")
	@Produces ( MediaType.APPLICATION_JSON)
	public Response getAllVets(){
		Collection<Vet> vets = new ArrayList<Vet>();
		vets.addAll(this.clinicService.findAllVets());
		if (vets.isEmpty()){
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(vets).build();
	}

	@RolesAllowed(Roles.VET_ADMIN)
	@GET
	@Path("/{vetId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVet(@PathParam("vetId") int vetId){
		Vet vet = this.clinicService.findVetById(vetId);
		if(vet == null){
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(vet).build();
	}

	@RolesAllowed(Roles.VET_ADMIN)
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Response addVet(@Valid Vet vet) {
		this.clinicService.saveVet(vet);
		return Response.status(Status.CREATED).entity(vet).build();
	}

	@RolesAllowed(Roles.VET_ADMIN)
	@PUT
	@Path("/{vetId}")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Response updateVet(@PathParam("vetId") int vetId, @Valid Vet vet) {
		Vet currentVet = this.clinicService.findVetById(vetId);
		if(currentVet == null){
			return Response.status(Status.NOT_FOUND).build();
		}
		currentVet.setFirstName(vet.getFirstName());
		currentVet.setLastName(vet.getLastName());
		currentVet.clearSpecialties();
		for(Specialty spec : vet.getSpecialties()) {
			currentVet.addSpecialty(spec);
		}
		this.clinicService.saveVet(currentVet);
		return Response.noContent().entity(currentVet).build();
	}

	@RolesAllowed(Roles.VET_ADMIN)
	@DELETE
	@Path("/{vetId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response deleteVet(@PathParam("vetId") int vetId){
		Vet vet = this.clinicService.findVetById(vetId);
		if(vet == null){
			return Response.status(Status.NOT_FOUND).build();
		}
		this.clinicService.deleteVet(vet);
		return Response.noContent().build();
	}



}
