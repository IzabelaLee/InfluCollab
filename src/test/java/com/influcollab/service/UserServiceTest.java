package com.influcollab.service;

import com.influcollab.entity.User;
import com.influcollab.exception.EmailAlreadyExistsException;
import com.influcollab.exception.UserNotFoundException;
import com.influcollab.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    @Test
    void shouldCreateUserAndEncodePassword() {
        User user = createUser();

        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";

        user.setPassword(rawPassword);

        when(repository.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(repository.save(user)).thenReturn(user);

        User result = service.createUser(user);

        assertEquals(user, result);
        assertEquals(encodedPassword, result.getPassword());

        verify(repository).existsByEmail(user.getEmail());
        verify(passwordEncoder).encode(rawPassword);
        verify(repository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithExistingEmail() {
        User user = createUser();

        when(repository.existsByEmail(user.getEmail()))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> service.createUser(user)
        );

        verify(repository).existsByEmail(user.getEmail());
        verify(passwordEncoder, never()).encode(any());
        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnAllUsers() {
        User user1 = createUser();
        User user2 = createUser();

        List<User> users = List.of(user1, user2);

        when(repository.findAll()).thenReturn(users);

        List<User> result = service.getAllUsers();

        assertEquals(users, result);

        verify(repository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoUsers() {
        when(repository.findAll()).thenReturn(List.of());

        List<User> result = service.getAllUsers();

        assertTrue(result.isEmpty());

        verify(repository).findAll();
    }

    @Test
    void shouldReturnUserById() {
        Long userId = 1L;

        User user = createUser();
        user.setId(userId);

        when(repository.findById(userId)).thenReturn(Optional.of(user));

        User result = service.getUserById(userId);

        assertEquals(user, result);

        verify(repository).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        Long userId = 999L;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> service.getUserById(userId)
        );

        verify(repository).findById(userId);
    }

    @Test
    void shouldUpdateUser() {
        Long userId = 1L;

        User existingUser = createUser();
        existingUser.setId(userId);

        User updatedUser = new User();
        updatedUser.setName("Updated Name");
        updatedUser.setSurname("Updated Surname");
        updatedUser.setChannelName("updatedChannel");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPhoneNumber("123456789");
        updatedUser.setCity("Warsaw");

        when(repository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(repository.save(existingUser)).thenReturn(existingUser);

        User result = service.updateUser(userId, updatedUser);

        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Surname", result.getSurname());
        assertEquals("updatedChannel", result.getChannelName());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("123456789", result.getPhoneNumber());
        assertEquals("Warsaw", result.getCity());

        verify(repository).findById(userId);
        verify(repository).save(existingUser);
    }


    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingUser() {
        Long userId = 999L;

        User updatedUser = new User();

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> service.updateUser(userId, updatedUser)
        );

        verify(repository).findById(userId);
        verify(repository, never()).save(any());
    }

    @Test
    void shouldDeleteUser() {
        Long userId = 1L;

        User user = createUser();
        user.setId(userId);

        when(repository.findById(userId)).thenReturn(Optional.of(user));

        service.deleteUser(userId);

        verify(repository).findById(userId);
        verify(repository).delete(user);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingUser() {
        Long userId = 999L;

        when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> service.deleteUser(userId)
        );

        verify(repository).findById(userId);
        verify(repository, never()).delete(any());
    }

    @Test
    void shouldReturnTrueWhenPasswordMatches() {
        String rawPassword = "password123";
        String hashedPassword = "hashedPassword";

        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);

        boolean result =
                service.verifyPassword(rawPassword, hashedPassword);

        assertTrue(result);

        verify(passwordEncoder).matches(rawPassword, hashedPassword);
    }

    @Test
    void shouldReturnFalseWhenPasswordDoesNotMatch() {
        String rawPassword = "wrongPassword";
        String hashedPassword = "hashedPassword";

        when(passwordEncoder.matches(rawPassword, hashedPassword)).thenReturn(false);

        boolean result = service.verifyPassword(rawPassword, hashedPassword);

        assertFalse(result);

        verify(passwordEncoder).matches(rawPassword, hashedPassword);
    }

    private User createUser() {
        User user = new User();

        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john@example.com");
        user.setChannelName("johnChannel");
        user.setPhoneNumber("123456789");
        user.setCity("Warsaw");

        return user;
    }
}