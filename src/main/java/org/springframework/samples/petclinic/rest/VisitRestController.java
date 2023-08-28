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
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.MediaType;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.samples.petclinic.model.Specialty;
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

	//@RolesAllowed(Roles.OWNER_ADMIN)
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getAllVisits(){
        return this.clinicService.findAllVisits()
            .onItem().ifNotNull().transform(visits -> Response.ok(visits).build())
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

	//@RolesAllowed(Roles.OWNER_ADMIN)
	@GET
	@Path("/{visitId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getVisit(@PathParam("visitId") int visitId){
        return this.clinicService.findVisitById(visitId)
            .onItem().ifNotNull().transform(visit -> Response.ok(visit).build())
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

	//@RolesAllowed(Roles.OWNER_ADMIN)
	@POST
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Uni<Response> addVisit(@Valid Visit visit) {
        return this.clinicService.saveVisit(visit).replaceWith(Response.status(Status.CREATED).entity(visit).build());
	}

	//@RolesAllowed(Roles.OWNER_ADMIN)
	@PUT
	@Path("/{visitId}")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Uni<Response> updateVisit(@PathParam("visitId") int visitId, @Valid Visit visit) {
        return this.clinicService.findVisitById(visitId)
            .onItem().ifNotNull().transformToUni(currentVisit -> {
                currentVisit.setDate(visit.getDate());
                currentVisit.setDescription(visit.getDescription());
                currentVisit.setPet(visit.getPet());
                return clinicService.saveVisit(currentVisit).onItem().transform(en->Response.noContent().entity(currentVisit).build());
            })
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

	//@RolesAllowed(Roles.OWNER_ADMIN)
	@DELETE
	@Path("/{visitId}")
	@Produces(MediaType.APPLICATION_JSON)
	@WithTransaction
	public Uni<Response> deleteVisit(@PathParam("visitId") int visitId){
        return this.clinicService.findVisitById(visitId)
            .onItem().ifNotNull().transformToUni(visit -> this.clinicService.deleteVisit(visit).onItem().transform(en-> Response.noContent().entity(visit).build()))
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

}
