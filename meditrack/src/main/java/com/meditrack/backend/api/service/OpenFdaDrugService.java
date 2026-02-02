package com.meditrack.backend.api.service;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.stereotype.Service;
import com.meditrack.backend.api.dto.OpenFdaLabelResponse;
import java.util.List;
import com.meditrack.backend.api.dto.DrugResult;

// Service to interact with OpenFDA API for drug information
@Service
public class OpenFdaDrugService {
    
    // RestTemplate for making HTTP requests
    private final RestTemplate restTemplate = new RestTemplate();
    // Search for drugs by name using OpenFDA API
    public List<DrugResult> searchDrug(String name) {
        try {
            // Construct the API URL - placeholder endpoint for public repo
            String url = "https://api.example.com/drug/label?search=generic_name:"
                        + name + "+brand_name:" + name + "&limit=5";
            // Make the API request and map the response
            OpenFdaLabelResponse response = restTemplate.getForObject(url, OpenFdaLabelResponse.class);

            if(response == null || response.getResults() == null) {
                return List.of();
            }
            // Map results to DrugResult DTOs
            return response.getResults().stream().map(this::mapToDto).toList();
        } catch (HttpClientErrorException e) {
            // If OpenFDA API returns 404 or any error, return empty list
            // This allows frontend to show user-friendly "no results" message
            return List.of();
        } catch (Exception e) {
            // For any other errors, also return empty list
            return List.of();
        }
    }
    // Helper method to map API result to DrugResult DTO
    private DrugResult mapToDto(OpenFdaLabelResponse.Result result) {
        DrugResult dto = new DrugResult();

        dto.setBrandName(first(result.getOpenfda().getBrand_name()));
        dto.setGenericName(first(result.getOpenfda().getGeneric_name()));
        dto.setPurpose(first(result.getPurpose()));
        dto.setIndications(first(result.getIndications_and_usage()));
        dto.setWarnings(first(result.getWarnings()));
        dto.setSideEffects(first(result.getAdverse_reactions()));
        dto.setDosage(first(result.getDosage_and_administration()));
        dto.setRoute(first(result.getOpenfda().getRoute()));
        dto.setRxcui(first(result.getOpenfda().getRxcui()));

        return dto;
    }

    // Helper to get the first element of a list or null if empty
    private String first(List<String> list) {
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }
}

