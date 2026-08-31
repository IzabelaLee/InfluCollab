package com.influcollab.service;

import com.influcollab.entity.User;
import com.influcollab.exception.UnauthorizedRequestException;
import com.influcollab.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthorizationService authorizationService;


    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnAuthenticatedUser() {

        User user = createUser();

        setAuthenticatedUser("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        User result = authorizationService.getAuthenticatedUser();

        assertNotNull(result);
        assertSame(user, result);

        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void shouldThrowExceptionWhenNoAuthenticationExists() {

        SecurityContextHolder.clearContext();

        assertThrows(UnauthorizedRequestException.class, () -> authorizationService.getAuthenticatedUser());
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldThrowExceptionWhenPrincipalIsNotUserDetails() {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("john@example.com", null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThrows(UnauthorizedRequestException.class, () -> authorizationService.getAuthenticatedUser());
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldThrowExceptionWhenAuthenticatedUserDoesNotExist() {

        setAuthenticatedUser("unknown@example.com");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedRequestException.class, () -> authorizationService.getAuthenticatedUser());
        verify(userRepository).findByEmail("unknown@example.com");
    }

    @Test
    void shouldVerifyOwnershipWhenUserIsOwner() {

        User user = createUser();

        setAuthenticatedUser("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> authorizationService.verifyOwnership(1L));
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void shouldRejectOwnershipWhenUserIsNotOwner() {

        User user = createUser();

        setAuthenticatedUser("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedRequestException.class, () -> authorizationService.verifyOwnership(999L));
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void shouldVerifyOpportunityOwnershipWhenUserIsOwner() {

        User user = createUser();

        setAuthenticatedUser("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> authorizationService.verifyOpportunityOwnership(1L));
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void shouldRejectOpportunityOwnershipWhenUserIsNotOwner() {

        User user = createUser();

        setAuthenticatedUser("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedRequestException.class, () -> authorizationService.verifyOpportunityOwnership(999L));
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void shouldReturnTrueWhenUserIsOwner() {

        User user = createUser();

        setAuthenticatedUser("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        assertTrue(authorizationService.isOwner(1L));
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void shouldReturnFalseWhenUserIsNotOwner() {

        User user = createUser();

        setAuthenticatedUser("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        assertFalse(authorizationService.isOwner(999L));
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void shouldReturnFalseWhenUserIsNotAuthenticated() {

        SecurityContextHolder.clearContext();

        assertFalse(authorizationService.isOwner(1L));
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldReturnSameResultForOpportunityOwner() {

        User user = createUser();

        setAuthenticatedUser("john@example.com");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        assertTrue(authorizationService.isOpportunityOwner(1L));
        assertFalse(authorizationService.isOpportunityOwner(999L));
        verify(userRepository, times(2)).findByEmail("john@example.com");
    }


    private void setAuthenticatedUser(String email) {

        when(userDetails.getUsername()).thenReturn(email);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private User createUser() {

        User user = new User();

        user.setId(1L);
        user.setName("John");
        user.setSurname("Smith");
        user.setChannelName("John's Channel");
        user.setEmail("john@example.com");
        user.setPassword("password123");

        return user;
    }
}