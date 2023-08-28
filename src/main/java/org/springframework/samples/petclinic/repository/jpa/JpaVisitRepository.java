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

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import org.springframework.samples.petclinic.model.Visit;
/**
 * JPA implementation of the ClinicService interface using EntityManager.
 * <p/>
 * <p>The mappings are defined in "orm.xml" located in the META-INF directory.
 *
 * @author Mike Keith
 * @author Rod Johnson
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@ApplicationScoped
public class JpaVisitRepository implements PanacheRepositoryBase<Visit,Integer>, PanacheRepositoryCustom<Visit> {

    public Uni<Void> save(Visit visit) {
        return Uni.createFrom().item(visit).flatMap(visit1 -> {
            if (visit1.getId() == null) {
                return persist(visit1).replaceWithVoid();
            } else {
                return merge(visit1).replaceWithVoid();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public Uni<List<Visit>> findByPetId(long petId) {
        PanacheQuery<Visit> id = this.find("SELECT v FROM Visit v where v.pet.id= :id", Parameters.with("id", petId));
        return id.list();
    }

    /**
     * Based on current hibernate model, visits deleting has problems
     * an assertion failure occurred (this may indicate a bug in Hibernate, but is more likely due to unsafe use of the session): org.hibernate.AssertionFailure: Unable to perform un-delete for instance org.springframework.samples.petclinic.model.Visit
     * so use JQL instead
     * @param visit
     * @return
     */
    public Uni<Void> deleteObject(Visit visit) {
        Uni<Long> id = this.delete("DELETE FROM Visit visit WHERE id=:id", Parameters.with("id", visit.getId()));
        return id.replaceWithVoid();
    }
}
