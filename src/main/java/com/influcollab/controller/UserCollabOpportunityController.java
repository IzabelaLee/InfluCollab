package com.influcollab.controller;

import com.influcollab.dto.CollabOpportunityDTO;
import com.influcollab.dto.UpdateCollabOpportunityDTO;
import com.influcollab.entity.CollabOpportunity;
import com.influcollab.service.CollabOpportunityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CollabOpportunityDTO> createCollabOpportunity(
            @PathVariable Long userId,
            @RequestBody @Valid CollabOpportunity collabOpportunity
    ) {
        CollabOpportunityDTO created = service.createCollabOpportunity(collabOpportunity, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{opportunityId}")
    public CollabOpportunityDTO updateCollabOpportunity(@PathVariable Long userId, @PathVariable Long opportunityId, @Valid
    @RequestBody CollabOpportunity collabOpportunity) {
        return service.updateCollabOpportunity(userId, opportunityId, collabOpportunity);
    }

    @PatchMapping("/{opportunityId}")
    public CollabOpportunityDTO updateCollabOpportunityDetail(
            @PathVariable Long userId,
            @PathVariable Long opportunityId,
            @Valid @RequestBody UpdateCollabOpportunityDTO updateDTO
    ) {
        return service.updateCollabOpportunityDetail(userId, opportunityId, updateDTO);
    }

    @DeleteMapping("/{opportunityId}")
    public ResponseEntity<Void> deleteCollabOpportunity(
            @PathVariable Long userId,
            @PathVariable Long opportunityId
    ) {
        service.deleteCollabOpportunity(userId, opportunityId);
        return ResponseEntity.noContent().build();
    }
}
