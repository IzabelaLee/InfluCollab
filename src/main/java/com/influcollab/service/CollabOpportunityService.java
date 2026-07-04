package com.influcollab.service;

import com.influcollab.dto.CollabOpportunityDTO;
import com.influcollab.dto.CollabOpportunityMapper;
import com.influcollab.dto.UpdateCollabOpportunityDTO;
import com.influcollab.entity.CollabOpportunity;
import com.influcollab.entity.User;
import com.influcollab.exception.CollabNotFoundException;
import com.influcollab.exception.UserNotFoundException;
import com.influcollab.repository.CollabOpportunityRepository;
import com.influcollab.repository.UserRepository;
import com.influcollab.repository.spec.CollabOpportunitySpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollabOpportunityService {

    private final CollabOpportunityRepository collabOpportunityRepository;
    private final UserRepository userRepository;
    private final CollabOpportunityMapper mapper;

    public CollabOpportunityService(
            CollabOpportunityRepository collabOpportunityRepository,
            UserRepository userRepository,
            CollabOpportunityMapper mapper
    ) {
        this.collabOpportunityRepository = collabOpportunityRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public List<CollabOpportunityDTO> getAllCollabOpportunities(String city, LocalDate from, LocalDate to, String ownerId) {
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

        if (ownerId != null && !ownerId.isBlank()) {
            spec = spec.and(CollabOpportunitySpecification.hasOwnerId(ownerId.trim()));
        }

        return collabOpportunityRepository.findAll(spec)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    public CollabOpportunityDTO getCollabOpportunityById(Long id) {
        CollabOpportunity opportunity = collabOpportunityRepository.findById(id)
                .orElseThrow(() -> new CollabNotFoundException(id));
        return mapper.toDTO(opportunity);
    }

    public List<CollabOpportunityDTO> getAllCollabOpportunitiesByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return collabOpportunityRepository.findByOwnerId(userId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public CollabOpportunityDTO getCollabOpportunityByIdAndUserId(Long userId, Long opportunityId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        CollabOpportunity opportunity = collabOpportunityRepository.findByIdAndOwnerId(opportunityId, userId)
                .orElseThrow(() -> new CollabNotFoundException(opportunityId));

        return mapper.toDTO(opportunity);
    }

    public CollabOpportunityDTO createCollabOpportunity(CollabOpportunity collabOpportunity, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(ownerId));

        collabOpportunity.setUser(owner);
        CollabOpportunity saved = collabOpportunityRepository.save(collabOpportunity);

        return mapper.toDTO(saved);
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
        return mapper.toDTO(updated);
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
        return mapper.toDTO(updated);
    }

    public void deleteCollabOpportunity(Long userId, Long opportunityId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        CollabOpportunity opportunity = collabOpportunityRepository.findByIdAndOwnerId(opportunityId, userId)
                .orElseThrow(() -> new CollabNotFoundException(opportunityId));

        collabOpportunityRepository.delete(opportunity);
    }
}
