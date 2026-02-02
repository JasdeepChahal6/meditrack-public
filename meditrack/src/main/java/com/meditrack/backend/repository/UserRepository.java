package com.meditrack.backend.repository;

import com.meditrack.backend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

//extends JpaRepository gives CRUD operations uses 2 parameters - Entity type for which we are creating repo for and PK of that entity
public interface UserRepository extends JpaRepository<User, Long> {
    //custom query method to find user by email
    Optional<User> findByEmail(String email);
}
