package com.influcollab.dto;

import com.influcollab.entity.CollabOpportunity;
import com.influcollab.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CollabOpportunityMapper {

    public CollabOpportunityDTO toDTO(CollabOpportunity opportunity) {
        if (opportunity == null) {
            return null;
        }

        User creator = opportunity.getUser();
        UserSummaryDTO creatorDTO = creator != null 
            ? new UserSummaryDTO(creator.getId(), creator.getName(), creator.getChannelName())
            : null;

        return new CollabOpportunityDTO(
            opportunity.getId(),
            opportunity.getTitle(),
            opportunity.getCity(),
            opportunity.getStartDate(),
            opportunity.getEndDate(),
            opportunity.getDescription(),
            opportunity.getCreatedAt(),
            creatorDTO
        );
    }
}
