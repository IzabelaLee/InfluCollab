package com.influcollab.controller;

import com.influcollab.dto.CollabOpportunityDTO;
import com.influcollab.service.CollabOpportunityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/opportunities")
public class CollabOpportunityController {

    private final CollabOpportunityService service;

    public CollabOpportunityController(CollabOpportunityService service) {
        this.service = service;
    }

    @GetMapping
    public List<CollabOpportunityDTO> getAllCollabOpportunities() {
        return service.getAllCollabOpportunities();
    }

    @GetMapping("/{id}")
    public CollabOpportunityDTO getCollabOpportunityById(@PathVariable Long id) {
        return service.getCollabOpportunityById(id);
    }
}
