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

	//@RolesAllowed(Roles.VET_ADMIN)
	@GET
	@Path("/")
	@Produces ( MediaType.APPLICATION_JSON)
	public Uni<Response> getAllVets(){
        return this.clinicService.findAllVets()
            .onItem().ifNotNull().transform(vets -> Response.ok(vets).build())
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

	//@RolesAllowed(Roles.VET_ADMIN)
	@GET
	@Path("/{vetId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Uni<Response> getVet(@PathParam("vetId") int vetId){
        return this.clinicService.findVetById(vetId)
            .onItem().ifNotNull().transform(vet -> Response.ok(vet).build())
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

	//@RolesAllowed(Roles.VET_ADMIN)
	@POST
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Uni<Response> addVet(@Valid Vet vet) {
        return this.clinicService.saveVet(vet).replaceWith(Response.status(Status.CREATED).entity(vet).build());
	}

	//@RolesAllowed(Roles.VET_ADMIN)
	@PUT
	@Path("/{vetId}")
	@Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
	public Uni<Response> updateVet(@PathParam("vetId") int vetId, @Valid Vet vet) {
        return this.clinicService.findVetById(vetId)
            .onItem().ifNotNull().transformToUni(currentVet -> {
                currentVet.setFirstName(vet.getFirstName());
                currentVet.setLastName(vet.getLastName());
                currentVet.clearSpecialties();
                for(Specialty spec : vet.getSpecialties()) {
                    currentVet.addSpecialty(spec);
                }
                return clinicService.saveVet(currentVet).onItem().transform(en-> Response.noContent().entity(currentVet).build());
            })
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}

	//@RolesAllowed(Roles.VET_ADMIN)
	@DELETE
	@Path("/{vetId}")
	@Produces(MediaType.APPLICATION_JSON)
	@WithTransaction
	public Uni<Response> deleteVet(@PathParam("vetId") int vetId){
        return this.clinicService.findVetById(vetId)
            .onItem().ifNotNull().transformToUni(vet -> clinicService.deleteVet(vet).onItem().transform(en-> Response.noContent().build()))
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
	}



}
