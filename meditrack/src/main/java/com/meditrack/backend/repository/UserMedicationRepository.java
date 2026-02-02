package com.meditrack.backend.repository;

import com.meditrack.backend.Model.UserMedication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.meditrack.backend.Model.User;
import java.util.*;

//interface to allow for automatic generation of CRUD operations on UserMedication entity at runtime
//extends JpaRepository gives CRUD operations uses 2 parameters - Entity type for which we are creating repo for and PK of that entity
@Repository
public interface UserMedicationRepository extends JpaRepository<UserMedication, Long>{

    //custom query method to find all UserMedication records associated with a specific userId
    List<UserMedication> findByUserId(Long userId);

    // fetch all medications for a specific User entity
    List<UserMedication> findByUser(User user);
}
