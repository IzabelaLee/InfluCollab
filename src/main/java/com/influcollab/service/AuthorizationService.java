package com.influcollab.service;

import com.influcollab.entity.User;
import com.influcollab.exception.UnauthorizedRequestException;
import com.influcollab.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private final UserRepository userRepository;

    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser() {
        try {
            UserDetails principal = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            String email = principal.getUsername();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UnauthorizedRequestException());
        } catch (ClassCastException | NullPointerException e) {
            throw new UnauthorizedRequestException();
        }
    }

    public void verifyOwnership(Long requestedUserId) {
        User authenticatedUser = getAuthenticatedUser();

        if (!authenticatedUser.getId().equals(requestedUserId)) {
            throw new UnauthorizedRequestException();
        }
    }

    public void verifyOpportunityOwnership(Long opportunityOwnerId) {
        User authenticatedUser = getAuthenticatedUser();

        if (!authenticatedUser.getId().equals(opportunityOwnerId)) {
            throw new UnauthorizedRequestException();
        }
    }

    public boolean isOwner(Long userId) {
        try {
            User authenticatedUser = getAuthenticatedUser();
            return authenticatedUser.getId().equals(userId);
        } catch (UnauthorizedRequestException e) {
            return false;
        }
    }

    public boolean isOpportunityOwner(Long opportunityUserId) {
        return isOwner(opportunityUserId);
    }
}
