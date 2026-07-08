package com.influcollab.service;

import com.influcollab.entity.CollaborationRequest;
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

}
