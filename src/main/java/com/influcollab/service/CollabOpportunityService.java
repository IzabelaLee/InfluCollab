package com.influcollab.service;

import com.influcollab.dto.CollabOpportunityDTO;
import com.influcollab.dto.CollabOpportunityMapper;
import com.influcollab.entity.CollabOpportunity;
import com.influcollab.entity.User;
import com.influcollab.exception.CollabNotFoundException;
import com.influcollab.exception.UserNotFoundException;
import com.influcollab.repository.CollabOpportunityRepository;
import com.influcollab.repository.UserRepository;
import org.springframework.stereotype.Service;

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

    public List<CollabOpportunityDTO> getAllCollabOpportunities() {
        return collabOpportunityRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public CollabOpportunityDTO getCollabOpportunityById(Long id) {
        CollabOpportunity opportunity = collabOpportunityRepository.findById(id)
                .orElseThrow(() -> new CollabNotFoundException(id));
        return mapper.toDTO(opportunity);
    }

    public List<CollabOpportunityDTO> getAllCollabOpportunitiesByUser(Long userId) {
        // Validate user exists first
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return collabOpportunityRepository.findByOwnerId(userId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public CollabOpportunityDTO getCollabOpportunityByIdAndUserId(Long userId, Long opportunityId) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Fetch opportunity and verify it belongs to this user
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
}
