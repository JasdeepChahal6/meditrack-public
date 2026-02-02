package com.meditrack.backend.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class UserMedicationCreate {
 
    @NotBlank(message = "Drug name is required")
    private String drugName;
    
    private String rxcui; // Optional - RxNorm ID from API
    
    @NotBlank(message = "Dosage is required")
    private String dosage;

    @NotBlank(message = "Frequency is required")
    private String frequency;

    private LocalDate startDate;
    private String instructions;

    public UserMedicationCreate() {
    }
    // constructor
    public UserMedicationCreate(String drugName, String rxcui, String dosage, String frequency, LocalDate startDate, String instructions) {
        this.drugName = drugName;
        this.rxcui = rxcui;
        this.dosage = dosage;
        this.frequency = frequency;
        this.startDate = startDate;
        this.instructions = instructions;
    }

    // getters and setters
    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getRxcui() {
        return rxcui;
    }

    public void setRxcui(String rxcui) {
        this.rxcui = rxcui;
    }

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
