package com.meditrack.backend.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import java.time.LocalDate;

@Entity
@Table(name = "user_medications")
public class UserMedication {

    // Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Store drug info from OpenFDA API (no FK to Medication table)
    private String drugName; // e.g., "Ibuprofen"
    private String rxcui; // RxNorm Concept Unique Identifier from API
    
    private String dosage; // e.g., "500mg"
    private String frequency; // e.g., "Twice a day"
    private LocalDate startDate; // Date when user started taking this medication
    private String instructions; // e.g., "Take with food"

    // Establish Many-to-One relationship with User entity
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) 
    private User user;

    // Constructors
    public UserMedication(String drugName, String rxcui, String dosage, String frequency, LocalDate startDate, String instructions, User user) {
        this.drugName = drugName;
        this.rxcui = rxcui;
        this.dosage = dosage;
        this.frequency = frequency;
        this.startDate = startDate;
        this.instructions = instructions;
        this.user = user;
    }

    //might need later for JPA 
    public UserMedication() {
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getDrugName() {
        return drugName;
    }

    public String getRxcui() {
        return rxcui;
    }

    public String getDosage() {
        return dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public String getInstructions() {
        return instructions;
    }

    public User getUser() {
        return user;
    }

    // Setters
    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public void setRxcui(String rxcui) {
        this.rxcui = rxcui;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setUser(User user) {
        this.user = user;
    }


    
    
}
