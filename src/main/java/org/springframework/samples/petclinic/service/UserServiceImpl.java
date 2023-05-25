package org.springframework.samples.petclinic.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.springframework.samples.petclinic.model.Role;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.repository.jpa.JpaUserRepository;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    JpaUserRepository userRepository;

    @Override
    @Transactional
    @Counted(name="accessDB")
    @Timed(name="processDB", unit= MetricUnits.MILLISECONDS)

    public void saveUser(User user) throws Exception {

        if(user.getRoles() == null || user.getRoles().isEmpty()) {
            throw new Exception("User must have at least a role set!");
        }

        for (Role role : user.getRoles()) {
            if(!role.getName().startsWith("ROLE_")) {
                role.setName("ROLE_" + role.getName());
            }

            if(role.getUser() == null) {
                role.setUser(user);
            }
        }

        userRepository.save(user);
    }
}
