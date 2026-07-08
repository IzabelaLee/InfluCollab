package com.influcollab.controller;

import com.influcollab.dto.CollabOpportunityDTO;
import com.influcollab.dto.CollaborationRequestDTO;
import com.influcollab.dto.CreateCollaborationRequestDTO;
import com.influcollab.service.CollabOpportunityService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/opportunities")
public class CollabOpportunityController {

    private final CollabOpportunityService service;

    public CollabOpportunityController(CollabOpportunityService service) {
        this.service = service;
    }

    @GetMapping
    public Page<CollabOpportunityDTO> getAllCollabOpportunities(@RequestParam(required = false) String city,
                                                                @RequestParam(required = false) LocalDate from,
                                                                @RequestParam(required = false) LocalDate to,
                                                                @RequestParam(required = false) Long ownerId,
                                                                @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return service.getAllCollabOpportunities(city, from, to, ownerId, pageable);
    }

    @GetMapping("/{opportunityId}")
    public CollabOpportunityDTO getCollabOpportunityById(@PathVariable Long opportunityId) {
        return service.getCollabOpportunityById(opportunityId);
    }

    @PostMapping("/{opportunityId}/requests")
    public ResponseEntity<CollaborationRequestDTO> createCollaborationRequest(@PathVariable Long opportunityId, @RequestBody @Valid CreateCollaborationRequestDTO requestDTO) {
        CollaborationRequestDTO created =
                service.createCollaborationRequest(opportunityId, requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(created);
    }
}
