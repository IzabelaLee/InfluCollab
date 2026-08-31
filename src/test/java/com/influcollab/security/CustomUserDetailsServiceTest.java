package com.influcollab.security;

import com.influcollab.entity.User;
import com.influcollab.enums.UserRole;
import com.influcollab.exception.UsernameNotFoundException;
import com.influcollab.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;


    @Test
    void shouldLoadUserByEmailSuccessfully() {

        User user = createUser();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("john@example.com");

        assertNotNull(result);
        assertInstanceOf(CustomUserDetails.class, result);
        assertEquals("john@example.com", result.getUsername());
        assertEquals("password123", result.getPassword());
        verify(userRepository).findByEmail("john@example.com");
    }


    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("unknown@example.com"));
        verify(userRepository).findByEmail("unknown@example.com");
    }


    @Test
    void shouldReturnUserWithCorrectRole() {

        User user = createUser();
        user.setRole(UserRole.USER_ROLE);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("john@example.com");

        assertEquals("USER_ROLE", result.getAuthorities().iterator().next().getAuthority());
        verify(userRepository).findByEmail("john@example.com");
    }


    @Test
    void shouldPassExactEmailToRepository() {

        User user = createUser();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        service.loadUserByUsername("john@example.com");

        verify(userRepository).findByEmail("john@example.com");
        verifyNoMoreInteractions(userRepository);
    }


    private User createUser() {

        User user = new User();

        user.setId(1L);
        user.setName("John");
        user.setSurname("Smith");
        user.setChannelName("John's Channel");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setRole(UserRole.USER_ROLE);

        return user;
    }
}
