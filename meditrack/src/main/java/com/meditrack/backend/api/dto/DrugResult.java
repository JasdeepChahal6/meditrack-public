package com.meditrack.backend.api.dto;

// Data Transfer Object for drug search results
public class DrugResult {
    
    private String brandName;
    private String genericName;
    private String purpose;
    private String indications;
    private String warnings;
    private String sideEffects;
    private String dosage;
    private String route; // e.g., oral, intravenous
    private String rxcui;

    // Getters
    public String getBrandName() {
        return brandName;
    }
    public String getGenericName() {
        return genericName;
    }
    public String getPurpose() {
        return purpose;
    }
    public String getIndications() {
        return indications;
    }
    public String getWarnings() {
        return warnings;
    }
    public String getSideEffects() {
        return sideEffects;
    }
    public String getDosage() {
        return dosage;
    }
    public String getRoute() {
        return route;
    }
    public String getRxcui() {
        return rxcui;
    }
    // Setters
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    public void setIndications(String indications) {
        this.indications = indications;
    }
    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }
    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    public void setRoute(String route) {
        this.route = route;
    }
    public void setRxcui(String rxcui) {
        this.rxcui = rxcui;
    }

}
