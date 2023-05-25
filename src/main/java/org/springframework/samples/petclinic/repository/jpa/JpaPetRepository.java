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

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

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
public class JpaPetRepository implements PanacheRepositoryBase<Pet, Integer> {
	@Inject
	EntityManager em;

	@SuppressWarnings("unchecked")
    public List<PetType> findPetTypes() {
        return this.em.createQuery("SELECT ptype FROM PetType ptype ORDER BY ptype.name").getResultList();
    }

	public void delete(Pet pet) {
		//this.em.remove(this.em.contains(pet) ? pet : this.em.merge(pet));
		String petId = pet.getId().toString();
		this.em.createQuery("DELETE FROM Visit visit WHERE pet_id=" + petId).executeUpdate();
		this.em.createQuery("DELETE FROM Pet pet WHERE id=" + petId).executeUpdate();
		if (em.contains(pet)) {
			em.remove(pet);
		}
	}

}
