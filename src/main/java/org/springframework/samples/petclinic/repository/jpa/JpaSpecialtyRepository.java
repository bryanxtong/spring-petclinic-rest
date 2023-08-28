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

import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.springframework.samples.petclinic.model.Specialty;



/**
 * @author Vitaliy Fedoriv
 *
 */
@ApplicationScoped
public class JpaSpecialtyRepository implements PanacheRepositoryBase<Specialty,Integer>,PanacheRepositoryCustom<Specialty> {

	public Uni<Void> save(Specialty specialty) {
        return Uni.createFrom().item(specialty).flatMap(specialty1 -> {
            if (specialty1.getId() == null) {
                return persist(specialty1).replaceWithVoid();
            } else {
                return this.merge(specialty1).replaceWithVoid();
            }
        });
	}

	public Uni<Void> deleteWithVetAssign(Specialty specialty) {
        Integer specId = specialty.getId();
        return Uni.createFrom().item(specialty).flatMap(specialty1 -> {
            Uni<Integer> integerUni = createNativeQuery("DELETE FROM vet_specialties WHERE specialty_id=" + specId).executeUpdate();
            return integerUni.onItem().transform(i-> specialty1);
        }).flatMap(specialty12 -> delete(specialty12)).replaceWithVoid();



    }

}
