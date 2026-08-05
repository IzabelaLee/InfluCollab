package com.influcollab.controller;

import com.influcollab.dto.CollabOpportunityDTO;
import com.influcollab.dto.UpdateCollabOpportunityDTO;
import com.influcollab.entity.CollabOpportunity;
import com.influcollab.service.AuthorizationService;
import com.influcollab.service.CollabOpportunityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/opportunities")
public class UserCollabOpportunityController {

    private final CollabOpportunityService service;
    private final AuthorizationService authorizationService;

    public UserCollabOpportunityController(CollabOpportunityService service, AuthorizationService authorizationService) {
        this.service = service;
        this.authorizationService = authorizationService;
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
        authorizationService.verifyOwnership(userId);

        CollabOpportunityDTO created = service.createCollabOpportunity(collabOpportunity, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{opportunityId}")
    public CollabOpportunityDTO updateCollabOpportunity(
            @PathVariable Long userId,
            @PathVariable Long opportunityId,
            @Valid @RequestBody CollabOpportunity collabOpportunity
    ) {
        authorizationService.verifyOwnership(userId);

        return service.updateCollabOpportunity(userId, opportunityId, collabOpportunity);
    }

    @PatchMapping("/{opportunityId}")
    public CollabOpportunityDTO updateCollabOpportunityDetail(
            @PathVariable Long userId,
            @PathVariable Long opportunityId,
            @Valid @RequestBody UpdateCollabOpportunityDTO updateDTO
    ) {
        authorizationService.verifyOwnership(userId);

        return service.updateCollabOpportunityDetail(userId, opportunityId, updateDTO);
    }

    @PreAuthorize("hasRole('ADMIN') or @authorizationService.isOpportunityOwner(#userId)")
    @DeleteMapping("/{opportunityId}")
    public ResponseEntity<Void> deleteCollabOpportunity(
            @PathVariable Long userId,
            @PathVariable Long opportunityId
    ) {
        service.deleteCollabOpportunity(userId, opportunityId);
        return ResponseEntity.noContent().build();
    }
}
