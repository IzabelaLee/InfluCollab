package com.influcollab.repository;

import com.influcollab.entity.CollabOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollabOpportunityRepository extends JpaRepository<CollabOpportunity, Long> {

    List<CollabOpportunity> findByOwnerId(Long ownerId);

    Optional<CollabOpportunity> findByIdAndOwnerId(Long id, Long ownerId);
}
