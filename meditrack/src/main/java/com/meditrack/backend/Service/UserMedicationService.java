package com.meditrack.backend.Service;

import com.meditrack.backend.repository.UserMedicationRepository;
import com.meditrack.backend.repository.UserRepository;
import com.meditrack.backend.Model.User;
import org.springframework.stereotype.Service;
import com.meditrack.backend.Model.UserMedication;
import java.util.*;


//@Service annotation to indicate that this class is a service component in Spring, making it eligible for component scanning and dependency injection
@Service
public class UserMedicationService {

    private final UserMedicationRepository userMedicationRepository;
    private final UserRepository userRepository;

    //dependency injection via constructor
    public UserMedicationService(UserMedicationRepository userMedicationRepository, UserRepository userRepository) {
        this.userMedicationRepository = userMedicationRepository;
        this.userRepository = userRepository;
    }
    
    public UserMedication save(UserMedication userMedication) {
        return userMedicationRepository.save(userMedication);
    }

    public List<UserMedication> getAll() {
        return userMedicationRepository.findAll();
    }

    public Optional<UserMedication> getById(Long id) {
        return userMedicationRepository.findById(id);
    }

    //Have to add method to repository because it's a custom query
    public List<UserMedication> getByUserId(Long userId) {
        return userMedicationRepository.findByUserId(userId);
    }

    public void deleteById(Long id) {
        userMedicationRepository.deleteById(id);
    }

    public List<UserMedication> getMedicationsByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userMedicationRepository.findByUserId(user.getId());
    }

    // Save medication for a user identified by email
    public UserMedication saveForUser(UserMedication medication, String email) {
        User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        medication.setUser(user);
        return userMedicationRepository.save(medication); 
    }

    // Get medications for a given user
    public List<UserMedication> getMedicationsForUser(User user) {
        return userMedicationRepository.findByUser(user);
    }
    
}
