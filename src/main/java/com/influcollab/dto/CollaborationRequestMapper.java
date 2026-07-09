package com.influcollab.dto;

import com.influcollab.entity.CollaborationRequest;
import org.springframework.stereotype.Component;

@Component
public class CollaborationRequestMapper {
    public CollaborationRequestDTO toDTO(CollaborationRequest request) {

        CollaborationRequestDTO dto =
                new CollaborationRequestDTO();

        dto.setId(request.getId());

        dto.setSenderId(request.getSender().getId());

        dto.setOpportunityId(request.getOpportunity().getId());

        dto.setMessage(request.getMessage());

        dto.setStatus(request.getStatus());

        dto.setCreatedAt(request.getCreatedAt());

        return dto;
    }
}
