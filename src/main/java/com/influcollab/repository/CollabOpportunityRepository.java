package com.influcollab.repository;

import com.influcollab.entity.CollabOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CollabOpportunityRepository extends JpaRepository<CollabOpportunity, Long>, JpaSpecificationExecutor<CollabOpportunity> {

    List<CollabOpportunity> findByOwnerId(Long ownerId);

    Optional<CollabOpportunity> findByIdAndOwnerId(Long id, Long ownerId);
}
