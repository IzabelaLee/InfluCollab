package com.influcollab.service;

import com.influcollab.dto.CollabOpportunityDTO;
import com.influcollab.dto.CollabOpportunityMapper;
import com.influcollab.entity.CollabOpportunity;
import com.influcollab.exception.CollabNotFoundException;
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
}
