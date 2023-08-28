package org.springframework.samples.petclinic.service;

import io.smallrye.mutiny.Uni;
import org.springframework.samples.petclinic.model.User;

public interface UserService {

    Uni<Void> saveUser(User user) throws Exception;
}
