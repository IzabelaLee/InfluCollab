package com.influcollab.controller;

import com.influcollab.dto.CollabOpportunityDTO;
import com.influcollab.service.CollabOpportunityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/{id}")
    public CollabOpportunityDTO getCollabOpportunityById(@PathVariable Long id) {
        return service.getCollabOpportunityById(id);
    }
}
