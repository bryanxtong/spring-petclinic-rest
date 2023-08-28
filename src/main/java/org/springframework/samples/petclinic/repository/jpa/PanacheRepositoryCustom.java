package org.springframework.samples.petclinic.repository.jpa;

import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.smallrye.common.annotation.CheckReturnValue;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;

/**
 * Extend only for hibernate reactive merge as It is missing in panache hibernate reactive library
 * @param <Entity>
 */
public interface PanacheRepositoryCustom<Entity> {

    @CheckReturnValue
    default Uni<Entity> merge(Entity entity) {
        return JpaOperationsCustom.INSTANCE.merge(entity).map((v) -> {
            return entity;
        });
    }

    @CheckReturnValue
    default Uni<Entity> mergeAndFlush(Entity entity) {
        return JpaOperationsCustom.INSTANCE.merge(entity).flatMap((v) -> {
            return JpaOperationsCustom.INSTANCE.flush();
        }).map((v) -> {
            return entity;
        });
    }
    @CheckReturnValue
    default Uni<? extends List<?>> findCustom(Class<?> entityClass, String query, Parameters params){
        PanacheQuery<?> panacheQuery =JpaOperationsCustom.INSTANCE.find(entityClass, query, params);
        return panacheQuery.list();
    }
    @CheckReturnValue
    default Mutiny.Query<?> createNativeQuery(String s){
        Mutiny.Query<?> nativeQuery = JpaOperationsCustom.INSTANCE.createNativeQuery(s);
        return nativeQuery;
    }
    @CheckReturnValue
    default Uni<Void> removeAll(Object... entities){
        return JpaOperationsCustom.INSTANCE.removeAll(entities);
    }


}
