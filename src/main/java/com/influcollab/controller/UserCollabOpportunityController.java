package com.influcollab.controller;

import com.influcollab.dto.CollabOpportunityDTO;
import com.influcollab.entity.CollabOpportunity;
import com.influcollab.service.CollabOpportunityService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/opportunities")
public class UserCollabOpportunityController {

    private final CollabOpportunityService service;

    public UserCollabOpportunityController(CollabOpportunityService service) {
        this.service = service;
    }

    @GetMapping
    public List<CollabOpportunityDTO> getUserAllOpportunities(@PathVariable Long userId) {
        return service.getAllCollabOpportunitiesByUser(userId);
    }

    @GetMapping("/{opportunityId}")
    public CollabOpportunityDTO getUserOpportunityById(
            @PathVariable Long userId,
            @PathVariable Long opportunityId
    ) {
        return service.getCollabOpportunityByIdAndUserId(userId, opportunityId);
    }

    @PostMapping
    public CollabOpportunityDTO createCollabOpportunity(
            @PathVariable Long userId,
            @RequestBody @Valid CollabOpportunity collabOpportunity
    ) {
        return service.createCollabOpportunity(collabOpportunity, userId);
    }
}
