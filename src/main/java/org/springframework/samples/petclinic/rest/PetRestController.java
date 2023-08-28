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
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.security.Roles;
import org.springframework.samples.petclinic.service.ClinicService;

/**
 * @author Vitaliy Fedoriv
 */

@Path("api/pets")
public class PetRestController {

    @Inject
    ClinicService clinicService;

    @Inject
    Validator validator;

    //@RolesAllowed( Roles.OWNER_ADMIN )
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> addPet(Pet pet) {
        Set<ConstraintViolation<Pet>> errors = validator.validate(pet);
        if (!errors.isEmpty() || (pet == null)) {
            Response error = Response.status(Status.BAD_REQUEST).header("errors", errors.stream().collect(Collectors.toMap(ConstraintViolation::getPropertyPath, ConstraintViolation::getMessage))).entity(pet).build();
            Uni<Response> item = Uni.createFrom().item(error);
            return item;
        }
        return this.clinicService.savePet(pet).onItem().ifNotNull().transform(entity -> Response.status(Status.CREATED).entity(entity).build());
    }

    //@RolesAllowed( Roles.OWNER_ADMIN )
    @GET
    @Path("/{petId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getPet(@PathParam("petId") int petId) {
        return this.clinicService.findPetById(petId)
            .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
    }

    //@RolesAllowed( Roles.OWNER_ADMIN )
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getPets() {
        return this.clinicService.findAllPets()
            .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
            .onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
    }

    //@RolesAllowed( Roles.OWNER_ADMIN )
    @GET
    @Path("/pettypes")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getPetTypes() {
        return this.clinicService.findPetTypes().onItem().ifNotNull().transform(petTypes -> Response.ok(petTypes).build());

    }

    //@RolesAllowed( Roles.OWNER_ADMIN )
    @PUT
    @Path("/{petId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> updatePet(@PathParam("petId") int petId, Pet pet) { //, BindingResult bindingResult){
        Set<ConstraintViolation<Pet>> errors = validator.validate(pet);

        if (!errors.isEmpty() || (pet == null)) {
            return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).header("errors", errors.stream().collect(Collectors.toMap(ConstraintViolation::getPropertyPath, ConstraintViolation::getMessage))).entity(pet).build());
        }
        return this.clinicService.findPetById(petId).onItem().ifNotNull().transformToUni(currentPet -> {
            currentPet.setBirthDate(pet.getBirthDate());
            currentPet.setName(pet.getName());
            currentPet.setType(pet.getType());
            currentPet.setOwner(pet.getOwner());
            return clinicService.savePet(currentPet).onItem().transform(en -> Response.noContent().build());
        }).onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
    }

    //@RolesAllowed( Roles.OWNER_ADMIN )
    @DELETE
    @Path("/{petId}")
    @Produces(MediaType.APPLICATION_JSON)
    @WithTransaction
    public Uni<Response> deletePet(@PathParam("petId") int petId) {
        return this.clinicService.findPetById(petId).onItem().ifNotNull().transformToUni(pet -> {
            return clinicService.deletePet(pet).onItem().transform(en -> Response.noContent().build());
        }).onItem().ifNull().continueWith(Response.status(Status.NOT_FOUND).build());
    }
}
