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

import org.springframework.samples.petclinic.model.Owner;


/**
 * JPA implementation of the {@link JpaOwnerRepository} interface.
 *
 * @author Mike Keith
 * @author Rod Johnson
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */

@ApplicationScoped
public class JpaOwnerRepository implements PanacheRepositoryBase<Owner, Integer>, PanacheRepositoryCustom<Owner> {

    /**
     * Important: in the current version of this method, we load Owners with all
     * their Pets and Visits while we do not need Visits at all and we only need one
     * property from the Pet objects (the 'name' property). There are some ways to
     * improve it such as: - creating a Ligtweight class (example here:
     * https://community.jboss.org/wiki/LightweightClass) - Turning on lazy-loading
     * and using {@link OpenSessionInViewFilter}
     */
    @SuppressWarnings("unchecked")
    public Uni<List<Owner>> findByLastName(String lastName) {
        // using 'join fetch' because a single query should load both owners and pets
        // using 'left join fetch' because it might happen that an owner does not have
        // pets yet
        PanacheQuery<Owner> ownerPanacheQuery = this.find(
            "SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.lastName LIKE :lastName",
            Parameters.with("lastName", lastName + "%"));
        return ownerPanacheQuery.list();
    }

    public Uni<Owner> findByIdLeftJoin(Integer id) {
        // using 'join fetch' because a single query should load both owners and pets
        // using 'left join fetch' because it might happen that an owner does not have
        // pets yet
        PanacheQuery<Owner> ownerPanacheQuery = this.find("SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id =:id",
            Parameters.with("id", id));
        return ownerPanacheQuery.firstResult();
    }

    public Uni<Void> save(Owner owner) {
        //the origial logic is if the id is null insert otherwise update
        return Uni.createFrom().item(owner.getId())
            .onItem().ifNotNull().transformToUni(integer -> {
                Uni<Owner> merge = merge(owner);
                return merge.onItem().transform(i-> owner);
            }).onItem().ifNull().switchTo(() -> {
                Uni<Owner> persist = persist(owner);
                return persist;
            }).replaceWithVoid();
       /* return Uni.createFrom().item(owner).log().flatMap(o -> {
            if (o.getId() == null) {
                return persist(o).replaceWithVoid();
            } else {
                return this.merge(o).replaceWithVoid();
            }
        });*/

    }

}
