package org.springframework.samples.petclinic.repository.jpa;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import org.springframework.samples.petclinic.model.User;

import java.util.List;
import java.util.function.Function;


@ApplicationScoped
public class JpaUserRepository implements PanacheRepositoryBase<User,Integer>, PanacheRepositoryCustom<User> {

    public Uni<Void> save(User user)  {
        PanacheQuery<User> panacheQuery = this.find("From User where username=:username", Parameters.with("username", user.getUsername()));
        return panacheQuery.firstResult().flatMap(new Function<User, Uni<?>>() {
            @Override
            public Uni<?> apply(User u) {
                if(u == null){
                    return persist(user);
                }
                else {
                    return merge(user);
                }
            }
        }).replaceWithVoid();
        /*return panacheQuery.list().flatMap((Function<List<User>, Uni<?>>) u -> {
            if(u == null){
                return persist(user);
            }
            else {
                return merge(user);
            }
        }).replaceWithVoid();*/
    }
}
