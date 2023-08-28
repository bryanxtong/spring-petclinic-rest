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

package org.springframework.samples.petclinic.repository.jpa;

import java.util.List;
import java.util.function.Function;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;

/**
 * @author Vitaliy Fedoriv
 *
 */
@ApplicationScoped
public class JpaPetTypeRepository implements PanacheRepositoryBase<PetType,Integer>, PanacheRepositoryCustom<PetType> {

    @SuppressWarnings("unchecked")
    public Uni<List<PetType>> findPetTypes() {
        return this.find("SELECT ptype FROM PetType ptype ORDER BY ptype.name").list();
    }

	public Uni<Void> save(PetType petType)  {
        return Uni.createFrom().item(petType).flatMap(petType1 -> {
            if (petType1.getId() == null) {
                return persist(petType1).replaceWithVoid();
            } else {
                return merge(petType1).replaceWithVoid();
            }
        });
	}

    /**
     * The original implementation deletes the pettypes as well as its connected pets and pets visits
     * @param petType
     * @return
     */
	@SuppressWarnings("unchecked")
	public Uni<Void> deleteObject(PetType petType)  {
		Uni<? extends List<?>> petTypes = this.findCustom(Pet.class, "SELECT pet FROM Pet pet WHERE type.id=:type_id", Parameters.with("type_id", petType.getId()));
        return petTypes.flatMap(new Function<List<?>, Uni<?>>() {
            @Override
            public Uni<?> apply(List<?> objects) {
                return removeAll(objects.toArray(new Object[0]));
            }
        }).flatMap(new Function<Object, Uni<?>>() {
            @Override
            public Uni<?> apply(Object o) {
                return delete(petType);
            }
        }).replaceWithVoid();
	}

}
