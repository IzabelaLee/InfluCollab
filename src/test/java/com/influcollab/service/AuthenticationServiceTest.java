package com.influcollab.service;

import com.influcollab.dto.LoginRequest;
import com.influcollab.entity.User;
import com.influcollab.exception.UsernameNotFoundException;
import com.influcollab.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void shouldAuthenticateUserSuccessfully() {

        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("password123");

        User user = createUser();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);

        User result = authenticationService.authenticate(request);

        assertNotNull(result);
        assertSame(user, result);

        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder).matches("password123", "encoded-password");
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authenticationService.authenticate(request));
        verify(userRepository).findByEmail("unknown@example.com");
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {

        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("wrong-password");

        User user = createUser();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThrows(UsernameNotFoundException.class, () -> authenticationService.authenticate(request));
        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder).matches("wrong-password", "encoded-password");
    }


    private User createUser() {

        User user = new User();

        user.setId(1L);
        user.setName("John");
        user.setSurname("Smith");
        user.setChannelName("John's Channel");
        user.setEmail("john@example.com");
        user.setPassword("encoded-password");

        return user;
    }
}