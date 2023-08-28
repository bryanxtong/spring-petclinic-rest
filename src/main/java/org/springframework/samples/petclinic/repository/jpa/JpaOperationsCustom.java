package org.springframework.samples.petclinic.repository.jpa;

import io.quarkus.hibernate.reactive.panache.common.runtime.SessionOperations;
import io.quarkus.hibernate.reactive.panache.runtime.JpaOperations;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

/**
 * There is no merge method in panache hibernate reactive implementation
 */
public class JpaOperationsCustom extends JpaOperations {
    public static final JpaOperationsCustom INSTANCE = new JpaOperationsCustom();

    public Uni<Void> merge(Object entity) {
        return merge(getSession(), entity);
    }

    public Uni<Void> merge(Uni<Mutiny.Session> sessionUni, Object entity) {
        return sessionUni.chain((session) -> {
            //using contain is wrong, doesnot know why
            //return session.contains(entity) ? session.merge(entity).replaceWithVoid(): Uni.createFrom().nullItem();
            return session.merge(entity).replaceWithVoid();
        });
    }

    public Uni<Void> removeAll(Object... entities) {
        return removeAll(getSession(), entities);
    }

    public Uni<Void> removeAll(Uni<Mutiny.Session> sessionUni, Object ... entities) {
        return sessionUni.chain((session) -> {
            return session.removeAll(entities);
        });
    }

    public Mutiny.Query<Object> createNativeQuery( String s){
        return this.getSessionCustom().createNativeQuery(s);
    }

    public Mutiny.Query<Object> createNativeQuery(Mutiny.Session session, String s){
            return session.createNativeQuery(s);
    }

    public Mutiny.Session getSessionCustom() {
        return SessionOperations.getCurrentSession();
    }


}
