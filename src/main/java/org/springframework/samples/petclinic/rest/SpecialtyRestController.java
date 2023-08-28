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
import org.springframework.samples.petclinic.model.PetType;
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

	//@RolesAllowed(Roles.VET_ADMIN)
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getAllSpecialtys(){
        return this.clinicService.findAllSpecialties()
            .onItem().ifNotNull().transform(specialties -> Response.ok(specialties).build())
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

	//@RolesAllowed(Roles.VET_ADMIN)
	@GET
	@Path("/{specialtyId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getSpecialty(@PathParam("specialtyId") int specialtyId){
        return this.clinicService.findSpecialtyById(specialtyId)
            .onItem().ifNotNull().transform(specialty -> Response.ok(specialty).build())
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

	//@RolesAllowed(Roles.VET_ADMIN)
	@POST
	@Path("")
	@Produces( MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Uni<Response> addSpecialty( @Valid Specialty specialty) {
        return this.clinicService.saveSpecialty(specialty).replaceWith(Response.status(Status.CREATED).entity(specialty).build());

	}

	//@RolesAllowed(Roles.VET_ADMIN)
	@PUT
	@Path("/{specialtyId}")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Uni<Response> updateSpecialty(@PathParam("specialtyId") int specialtyId,@Valid Specialty specialty) {
        return this.clinicService.findSpecialtyById(specialtyId)
            .onItem().ifNotNull().transformToUni(currentSpecialty -> {
                currentSpecialty.setName(specialty.getName());
                return clinicService.saveSpecialty(currentSpecialty).onItem().transform(en-> Response.noContent().entity(currentSpecialty).build());
            })
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

	//@RolesAllowed(Roles.VET_ADMIN)
	@DELETE
	@Path("/{specialtyId}")
	@Produces(MediaType.APPLICATION_JSON)
	@WithTransaction
	public Uni<Response> deleteSpecialty(@PathParam("specialtyId") int specialtyId){
        return this.clinicService.findSpecialtyById(specialtyId)
            .onItem().ifNotNull().transformToUni(currentSpecialty -> {
                return clinicService.deleteSpecialty(currentSpecialty).onItem().transform(en->Response.noContent().entity(currentSpecialty).build());
            })
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

}
