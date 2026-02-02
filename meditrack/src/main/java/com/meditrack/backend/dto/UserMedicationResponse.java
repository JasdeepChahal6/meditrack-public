package com.meditrack.backend.dto;

import java.time.LocalDate;

public class UserMedicationResponse {
    
    private Long id;
    private String drugName;
    private String rxcui;
    private String dosage;
    private String frequency;
    private LocalDate startDate;
    private String instructions;

    public UserMedicationResponse() {
    }

    // Constructor
    public UserMedicationResponse(Long id, String drugName, String rxcui, String dosage, String frequency, LocalDate startDate, String instructions) {
        this.id = id;
        this.drugName = drugName;
        this.rxcui = rxcui;
        this.dosage = dosage;
        this.frequency = frequency;
        this.startDate = startDate;
        this.instructions = instructions;
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
}
