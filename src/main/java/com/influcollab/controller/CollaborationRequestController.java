package com.influcollab.controller;

import com.influcollab.entity.CollaborationRequest;
import com.influcollab.service.CollaborationRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class CollaborationRequestController {
    private final CollaborationRequestService service;

    public CollaborationRequestController(CollaborationRequestService service) {
        this.service = service;
    }

    @GetMapping("/sent")
    public List<CollaborationRequest> getSentRequests(@RequestParam(required = true) Long userId) {
        return service.getSentRequests(userId);
    }

    @GetMapping("/received")
    public List<CollaborationRequest> getReceivedRequests(@RequestParam(required = true) Long ownerId) {
        return service.getReceivedRequests(ownerId);
    }

    @PutMapping("/{requestId}/accept")
    public CollaborationRequest acceptRequest(@PathVariable Long requestId, @RequestParam Long ownerId) {
        return service.acceptRequest(requestId, ownerId);
    }

    @PutMapping("/{requestId}/reject")
    public CollaborationRequest rejectRequest(@PathVariable Long requestId, @RequestParam Long ownerId) {
        return service.rejectRequest(requestId, ownerId);
    }
}
