package com.meditrack.backend.api.dto;

import java.util.List;

// raw response mapping for OpenFDA drug label API
public class OpenFdaLabelResponse {

    // top level results list from API response
    private List<Result> results;

    public OpenFdaLabelResponse() {
    }

    public List<Result> getResults() {
        return results;
    }
    public void setResults(List<Result> results) {
        this.results = results;
    }

    // single drug label result
    public static class Result {

        private List<String> purpose;
        private List<String> indications_and_usage;
        private List<String> warnings;
        private List<String> adverse_reactions;
        private List<String> dosage_and_administration;
        // nested openfda object
        private OpenFda openfda;

        public Result() {
        }

        public List<String> getPurpose() {
            return purpose;
        }

        public void setPurpose(List<String> purpose) {
            this.purpose = purpose;
        }

        public List<String> getIndications_and_usage() {
            return indications_and_usage;
        }

        public void setIndications_and_usage(List<String> indications_and_usage) {
            this.indications_and_usage = indications_and_usage;
        }

        public List<String> getWarnings() {
            return warnings;
        }

        public void setWarnings(List<String> warnings) {
            this.warnings = warnings;
        }

        public List<String> getAdverse_reactions() {
            return adverse_reactions;
        }

        public void setAdverse_reactions(List<String> adverse_reactions) {
            this.adverse_reactions = adverse_reactions;
        }

        public List<String> getDosage_and_administration() {
            return dosage_and_administration;
        }

        public void setDosage_and_administration(List<String> dosage_and_administration) {
            this.dosage_and_administration = dosage_and_administration;
        }

        public OpenFda getOpenfda() {
            return openfda;
        }

        public void setOpenfda(OpenFda openfda) {
            this.openfda = openfda;
        }
    }
    
    // nested openfda details
    public static class OpenFda {
        private List<String> brand_name;
        private List<String> generic_name;
        private List<String> route;
        private List<String> rxcui;

        public OpenFda() {
        }

        public List<String> getBrand_name() {
            return brand_name;
        }
        public void setBrand_name(List<String> brand_name) {
            this.brand_name = brand_name;
        }
        public List<String> getGeneric_name() {
            return generic_name;
        }
        public void setGeneric_name(List<String> generic_name) {
            this.generic_name = generic_name;
        }
        public List<String> getRoute() {
            return route;
        }
        public void setRoute(List<String> route) {
            this.route = route;
        }
        public List<String> getRxcui() {
            return rxcui;
        }
        public void setRxcui(List<String> rxcui) {
            this.rxcui = rxcui;
        }
    }    


}
