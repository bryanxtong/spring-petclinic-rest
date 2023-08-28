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

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.springframework.samples.petclinic.model.Vet;


/**
 * JPA implementation of the {@link JpaVetRepository} interface.
 *
 * @author Mike Keith
 * @author Rod Johnson
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@ApplicationScoped
public class JpaVetRepository implements PanacheRepositoryBase<Vet,Integer>, PanacheRepositoryCustom<Vet> {

	public Uni<Void> save(Vet vet)  {
        return Uni.createFrom().item(vet).flatMap(vet1 -> {
            if (vet1.getId() == null) {
                return persist(vet1).replaceWithVoid();
            } else {
                return merge(vet1).replaceWithVoid();
            }
        });
	}

}
