package com.meditrack.backend.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.meditrack.backend.api.dto.DrugResult;
import com.meditrack.backend.api.service.OpenFdaDrugService;
import java.util.List;

// REST controller for drug search endpoints via OpenFDA API
@RestController
@RequestMapping("/api/drugs")
public class DrugSearchController {
    
    private final OpenFdaDrugService drugService;

    // Constructor injection of the drug service
    public DrugSearchController(OpenFdaDrugService drugService) {
        this.drugService = drugService;
    }

    @GetMapping("/search")
    public List<DrugResult> search(@RequestParam String name) {
        return drugService.searchDrug(name);
    }
}
