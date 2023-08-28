/*
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.samples.petclinic.repository.jpa;

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;

import java.util.function.Consumer;
import java.util.function.Function;


/**
 * JPA implementation of the {@link JpaPetRepository} interface.
 *
 * @author Mike Keith
 * @author Rod Johnson
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@ApplicationScoped
public class JpaPetRepository implements PanacheRepositoryBase<Pet, Integer>, PanacheRepositoryCustom<Pet> {

    public Uni<Void> save(Pet pet) {
        return Uni.createFrom().item(pet).log().flatMap(pet1 -> {
            if (pet1.getId() == null) {
                return persist(pet1).replaceWithVoid();
            } else {
                return this.merge(pet1).replaceWithVoid();
            }
        });
    }
    public Uni<Void> deleteObject(Pet pet) {
       /* return this.findById(pet.getId()).flatMap(pet1 -> {
            return delete("DELETE FROM Visit visit WHERE visit.pet.id=:pet_id", Parameters.with("pet_id", pet1.getId())).onItem().transform(i -> pet1);
        }).flatMap(pet2 -> {
            return delete("DELETE FROM Pet pet WHERE id=:id", Parameters.with("id", pet2.getId()));
        }).replaceWithVoid();*/
        return delete(pet).replaceWithVoid();
    }

}
