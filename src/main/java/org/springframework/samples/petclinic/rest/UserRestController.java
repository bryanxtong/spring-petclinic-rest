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

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.security.Roles;
import org.springframework.samples.petclinic.service.UserService;

@Path("api/users")
public class UserRestController {

    @Inject
    UserService userService;

    @Inject
    Validator validator;

    //@RolesAllowed(Roles.ADMIN)
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> addOwner(@Valid User user) throws Exception {
        /**
         * {
         *   "username": "bryan",
         *   "password": "bryan",
         *   "enabled": true,
         *   "roles": [
         *     {
         *       "name": "ROLE_OWNER_ADMIN"
         *     }
         *   ]
         * }
         */
        return userService.saveUser(user).replaceWith(Response.status(Status.CREATED).entity(user).build());
    }
}
