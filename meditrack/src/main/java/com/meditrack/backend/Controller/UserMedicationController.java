package com.meditrack.backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import com.meditrack.backend.Service.UserMedicationService;
import com.meditrack.backend.dto.UserMedicationCreate;
import com.meditrack.backend.dto.UserMedicationResponse;
import com.meditrack.backend.dto.UserMedicationUpdate;
import com.meditrack.backend.repository.UserRepository;
import com.meditrack.backend.Model.UserMedication;
import com.meditrack.backend.Model.User;
import java.util.*;

@RestController
@RequestMapping("/user-medications")
public class UserMedicationController {

    // Dependency injection of UserMedicationService and UserRepository
    private final UserMedicationService userMedicationService;
    private final UserRepository userRepository;

    // Constructor for dependency injection
    public UserMedicationController(UserMedicationService userMedicationService, UserRepository userRepository) {
        this.userMedicationService = userMedicationService;
        this.userRepository = userRepository;
    }

    //get all medication trackers for a specific user by userId (will be a /me endpoint later)
    @GetMapping("/me")
    public ResponseEntity<List<UserMedicationResponse>> getMyMedications(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); // get current auth info
        String email = auth.getName(); // comes from JWT

        // find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // fetch medications for that user
        List<UserMedication> medications = userMedicationService.getMedicationsForUser(user);

        List<UserMedicationResponse> response = medications.stream()
                .map(med -> new UserMedicationResponse(
                        med.getId(),
                        med.getDrugName(),
                        med.getRxcui(),
                        med.getDosage(),
                        med.getFrequency(),
                        med.getStartDate(),
                        med.getInstructions()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
    //create new user medication tracker
    @PostMapping
    public ResponseEntity<UserMedicationResponse> addUserMedication(@RequestBody UserMedicationCreate dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); // get current auth info
        String email = auth.getName();

        // find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        UserMedication userMed = new UserMedication();
        userMed.setDrugName(dto.getDrugName());
        userMed.setRxcui(dto.getRxcui());
        userMed.setDosage(dto.getDosage());
        userMed.setFrequency(dto.getFrequency());
        userMed.setStartDate(dto.getStartDate());
        userMed.setInstructions(dto.getInstructions());
        userMed.setUser(user);

        // save medication linked to user
        UserMedication savedMedication = userMedicationService.save(userMed);
        
        UserMedicationResponse response = new UserMedicationResponse(
            savedMedication.getId(),
            savedMedication.getDrugName(),
            savedMedication.getRxcui(),
            savedMedication.getDosage(),
            savedMedication.getFrequency(),
            savedMedication.getStartDate(),
            savedMedication.getInstructions()
        );      
        return ResponseEntity.status(201).body(response);
    }


    //updates user medication tracker
    @PatchMapping("/{id}")
    //args are id of medication to update and map of fields to update
    public ResponseEntity<UserMedicationResponse> partiallyUpdateUserMedication(@PathVariable Long id, @RequestBody UserMedicationUpdate dto) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); // get current auth info
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserMedication existing = userMedicationService.getById(id)
            .orElseThrow(() -> new IllegalArgumentException("Medication not found"));

        // Check ownership
        if (!existing.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        // Apply updates if provided
        if (dto.getDosage() != null && !dto.getDosage().isBlank()) {
            existing.setDosage(dto.getDosage());
        }
        if (dto.getFrequency() != null && !dto.getFrequency().isBlank()) {
            existing.setFrequency(dto.getFrequency());
        }
        if (dto.getStartDate() != null) {
            existing.setStartDate(dto.getStartDate());
        }
        if (dto.getInstructions() != null && !dto.getInstructions().isBlank()) {
            existing.setInstructions(dto.getInstructions());
        }
        UserMedication updated = userMedicationService.save(existing);

        // Map to response DTO
        UserMedicationResponse response = new UserMedicationResponse(
                updated.getId(),
                updated.getDrugName(),
                updated.getRxcui(),
                updated.getDosage(),
                updated.getFrequency(),
                updated.getStartDate(),
                updated.getInstructions()
        );

        return ResponseEntity.ok(response);
    }   

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserMedication(@PathVariable Long id) {
        
        // get authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        UserMedication existing = userMedicationService.getById(id)
            .orElseThrow(() -> new IllegalArgumentException("Medication not found"));

        if (!existing.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        userMedicationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
}
