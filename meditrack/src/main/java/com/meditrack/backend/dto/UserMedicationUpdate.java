package com.meditrack.backend.dto;

import java.time.LocalDate;

public class UserMedicationUpdate {
    
    private String dosage;
    private String frequency;
    private LocalDate startDate;
    private String instructions;

    // default constructor
    public UserMedicationUpdate() {
    }

    // constructor
    public UserMedicationUpdate(String dosage, String frequency, LocalDate startDate, String instructions) {
        this.dosage = dosage;
        this.frequency = frequency;
        this.startDate = startDate;
        this.instructions = instructions;
    }

    // Getters and Setters
    public String getDosage() {
        return dosage;
    }
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    public String getFrequency() {
        return frequency;
    }
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public String getInstructions() {
        return instructions;
    }
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
