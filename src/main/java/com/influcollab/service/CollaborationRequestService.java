package com.influcollab.service;

import com.influcollab.entity.CollaborationRequest;
import com.influcollab.enums.CollaborationRequestStatus;
import com.influcollab.exception.CollaborationRequestNotFound;
import com.influcollab.exception.UnauthorizedRequestException;
import com.influcollab.repository.CollabOpportunityRepository;
import com.influcollab.repository.CollaborationRequestRepository;
import com.influcollab.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollaborationRequestService {

    public final UserRepository userRepository;
    public final CollabOpportunityRepository collabOpportunityRepository;
    public final CollaborationRequestRepository collaborationRequestRepository;

    public CollaborationRequestService(UserRepository userRepository, CollabOpportunityRepository collabOpportunityRepository, CollaborationRequestRepository collaborationRequestRepository) {
        this.userRepository = userRepository;
        this.collabOpportunityRepository = collabOpportunityRepository;
        this.collaborationRequestRepository = collaborationRequestRepository;
    }

    public List<CollaborationRequest> getSentRequests(Long userId) {
        return collaborationRequestRepository.findBySenderId(userId);
    }

    public List<CollaborationRequest> getReceivedRequests(Long ownerId) {
        return collaborationRequestRepository.findByOpportunityOwnerId(ownerId);
    }

    public CollaborationRequest acceptRequest(Long requestId, Long ownerId) {
        CollaborationRequest request = collaborationRequestRepository.findById(requestId)
                .orElseThrow(() -> new CollaborationRequestNotFound());
        if (!request.getOpportunity().getUser().getId().equals(ownerId)) {
            throw new UnauthorizedRequestException();
        }
        request.setStatus(CollaborationRequestStatus.ACCEPTED);

        return collaborationRequestRepository.save(request);
    }

    public CollaborationRequest rejectRequest(Long requestId, Long ownerId) {
        CollaborationRequest request = collaborationRequestRepository.findById(requestId)
                .orElseThrow(() -> new CollaborationRequestNotFound());
        if (!request.getOpportunity().getUser().getId().equals(ownerId)) {
            throw new UnauthorizedRequestException();
        }
        request.setStatus(CollaborationRequestStatus.REJECTED);
        return collaborationRequestRepository.save(request);
    }
}
