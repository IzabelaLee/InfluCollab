package com.influcollab.service;

import com.influcollab.dto.*;
import com.influcollab.entity.CollabOpportunity;
import com.influcollab.entity.CollaborationRequest;
import com.influcollab.entity.User;
import com.influcollab.exception.CollabNotFoundException;
import com.influcollab.exception.InvalidCollaborationRequestException;
import com.influcollab.exception.UserNotFoundException;
import com.influcollab.repository.CollabOpportunityRepository;
import com.influcollab.repository.CollaborationRequestRepository;
import com.influcollab.repository.UserRepository;
import com.influcollab.repository.spec.CollabOpportunitySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollabOpportunityService {

    private final CollabOpportunityRepository collabOpportunityRepository;
    private final UserRepository userRepository;
    private final CollaborationRequestRepository collaborationRequestRepository;
    private final CollabOpportunityMapper collabOpportunityMapper;
    private final CollaborationRequestMapper collaborationRequestMapper;

    public CollabOpportunityService(
            CollabOpportunityRepository collabOpportunityRepository,
            UserRepository userRepository,
            CollaborationRequestRepository collaborationRequestRepository,
            CollabOpportunityMapper collabOpportunityMapper,
            CollaborationRequestMapper collaborationRequestMapper
    ) {
        this.collabOpportunityRepository = collabOpportunityRepository;
        this.userRepository = userRepository;
        this.collaborationRequestRepository = collaborationRequestRepository;
        this.collabOpportunityMapper = collabOpportunityMapper;
        this.collaborationRequestMapper = collaborationRequestMapper;
    }

    public Page<CollabOpportunityDTO> getAllCollabOpportunities(String city, LocalDate from, LocalDate to, Long ownerId, Pageable pageable) {
        Specification<CollabOpportunity> spec = Specification.where(null);

        if (city != null && !city.isBlank()) {
            spec = spec.and(CollabOpportunitySpecification.hasCity(city.trim()));
        }

        if (from != null) {
            spec = spec.and(CollabOpportunitySpecification.startsOnOrAfter(from));
        }

        if (to != null) {
            spec = spec.and(CollabOpportunitySpecification.endsOnOrBefore(to));
        }

        if (ownerId != null) {
            spec = spec.and(CollabOpportunitySpecification.hasOwnerId(ownerId));
        }

        Page<CollabOpportunity> page =
                collabOpportunityRepository.findAll(spec, pageable);

        return page.map(collabOpportunityMapper::toDTO);
    }

    public CollabOpportunityDTO getCollabOpportunityById(Long id) {
        CollabOpportunity opportunity = collabOpportunityRepository.findById(id)
                .orElseThrow(() -> new CollabNotFoundException(id));
        return collabOpportunityMapper.toDTO(opportunity);
    }

    public List<CollabOpportunityDTO> getAllCollabOpportunitiesByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return collabOpportunityRepository.findByOwnerId(userId)
                .stream()
                .map(collabOpportunityMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CollabOpportunityDTO getCollabOpportunityByIdAndUserId(Long userId, Long opportunityId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        CollabOpportunity opportunity = collabOpportunityRepository.findByIdAndOwnerId(opportunityId, userId)
                .orElseThrow(() -> new CollabNotFoundException(opportunityId));

        return collabOpportunityMapper.toDTO(opportunity);
    }

    public CollabOpportunityDTO createCollabOpportunity(CollabOpportunity collabOpportunity, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(ownerId));

        collabOpportunity.setUser(owner);
        CollabOpportunity saved = collabOpportunityRepository.save(collabOpportunity);

        return collabOpportunityMapper.toDTO(saved);
    }

    public CollabOpportunityDTO updateCollabOpportunity(Long userId, Long opportunityId, CollabOpportunity collabOpportunity) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        CollabOpportunity existing = collabOpportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new CollabNotFoundException(opportunityId));

        existing.setTitle(collabOpportunity.getTitle());
        existing.setCity(collabOpportunity.getCity());
        existing.setStartDate(collabOpportunity.getStartDate());
        existing.setEndDate(collabOpportunity.getEndDate());
        existing.setDescription(collabOpportunity.getDescription());

        CollabOpportunity updated = collabOpportunityRepository.save(existing);
        return collabOpportunityMapper.toDTO(updated);
    }

    public CollabOpportunityDTO updateCollabOpportunityDetail(Long userId, Long opportunityId, UpdateCollabOpportunityDTO updateDTO) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        CollabOpportunity opportunity = collabOpportunityRepository.findByIdAndOwnerId(opportunityId, userId)
                .orElseThrow(() -> new CollabNotFoundException(opportunityId));

        if (updateDTO.getTitle() != null && !updateDTO.getTitle().isBlank()) {
            opportunity.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getCity() != null && !updateDTO.getCity().isBlank()) {
            opportunity.setCity(updateDTO.getCity());
        }
        if (updateDTO.getStartDate() != null) {
            opportunity.setStartDate(updateDTO.getStartDate());
        }
        if (updateDTO.getEndDate() != null) {
            opportunity.setEndDate(updateDTO.getEndDate());
        }
        if (updateDTO.getDescription() != null) {
            opportunity.setDescription(updateDTO.getDescription());
        }

        CollabOpportunity updated = collabOpportunityRepository.save(opportunity);
        return collabOpportunityMapper.toDTO(updated);
    }

    public void deleteCollabOpportunity(Long userId, Long opportunityId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        CollabOpportunity opportunity = collabOpportunityRepository.findByIdAndOwnerId(opportunityId, userId)
                .orElseThrow(() -> new CollabNotFoundException(opportunityId));

        collabOpportunityRepository.delete(opportunity);
    }

    public CollaborationRequestDTO createCollaborationRequest(Long opportunityId, CreateCollaborationRequestDTO requestDTO) {

        User sender = userRepository.findById(requestDTO.getSenderId())
                .orElseThrow(() -> new UserNotFoundException(requestDTO.getSenderId()));

        CollabOpportunity opportunity = collabOpportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new CollabNotFoundException(opportunityId));

        if (opportunity.getUser().getId().equals(sender.getId())) {
            throw new InvalidCollaborationRequestException();
        }

        CollaborationRequest collaborationRequest = new CollaborationRequest(sender, opportunity, requestDTO.getMessage());
        CollaborationRequest saved = collaborationRequestRepository.save(collaborationRequest);

        return collaborationRequestMapper.toDTO(saved);
    }
}
