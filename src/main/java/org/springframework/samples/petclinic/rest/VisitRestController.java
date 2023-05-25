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
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.MediaType;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.security.Roles;
import org.springframework.samples.petclinic.service.ClinicService;

/**
 * @author Vitaliy Fedoriv
 *
 */

@Path("api/visits")
public class VisitRestController {

	@Inject
	ClinicService clinicService;

	@RolesAllowed(Roles.OWNER_ADMIN)
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllVisits(){
		Collection<Visit> visits = new ArrayList<Visit>();
		visits.addAll(this.clinicService.findAllVisits());
		if (visits.isEmpty()){
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(visits).build();
	}

	@RolesAllowed(Roles.OWNER_ADMIN)
	@GET
	@Path("/{visitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVisit(@PathParam("visitId") int visitId){
		Visit visit = this.clinicService.findVisitById(visitId);
		if(visit == null){
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(visit).build();
	}

	@RolesAllowed(Roles.OWNER_ADMIN)
	@POST
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Response addVisit(@Valid Visit visit) {
		this.clinicService.saveVisit(visit);
		return Response.status(Status.CREATED).entity(visit).build();
	}

	@RolesAllowed(Roles.OWNER_ADMIN)
	@PUT
	@Path("/{visitId}")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Response updateVisit(@PathParam("visitId") int visitId, @Valid Visit visit) {
		Visit currentVisit = this.clinicService.findVisitById(visitId);
		if(currentVisit == null){
			return Response.status(Status.NOT_FOUND).build();
		}
		currentVisit.setDate(visit.getDate());
		currentVisit.setDescription(visit.getDescription());
		currentVisit.setPet(visit.getPet());
		this.clinicService.saveVisit(currentVisit);
		return Response.noContent().entity(currentVisit).build();
	}

	@RolesAllowed(Roles.OWNER_ADMIN)
	@DELETE
	@Path("/{visitId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response deleteVisit(@PathParam("visitId") int visitId){
		Visit visit = this.clinicService.findVisitById(visitId);
		if(visit == null){
			return Response.status(Status.NOT_FOUND).build();
		}
		this.clinicService.deleteVisit(visit);
		return Response.noContent().build();
	}

}
