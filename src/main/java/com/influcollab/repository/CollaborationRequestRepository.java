package com.influcollab.repository;

import com.influcollab.entity.CollaborationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollaborationRequestRepository extends JpaRepository<CollaborationRequest, Long> {
    List<CollaborationRequest> findByOpportunityId(Long opportunityId);

    List<CollaborationRequest> findBySenderId(Long senderId);

    List<CollaborationRequest> findByOpportunityOwnerId(Long ownerId);
}
