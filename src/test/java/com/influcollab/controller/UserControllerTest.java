package com.influcollab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.influcollab.entity.User;
import com.influcollab.enums.UserRole;
import com.influcollab.security.CustomUserDetailsService;
import com.influcollab.service.AuthorizationService;
import com.influcollab.service.JwtService;
import com.influcollab.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService service;

    @MockBean
    private AuthorizationService authorizationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    void shouldCreateUserSuccessfully() throws Exception {

        User user = createValidUser();
        User created = createValidUser();
        created.setId(1L);

        when(service.createUser(any(User.class))).thenReturn(created);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Smith"))
                .andExpect(jsonPath("$.channelName").value("John's Channel"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("USER_ROLE"));

        verify(service).createUser(any(User.class));
    }

    @Test
    void shouldGetAllUsers() throws Exception {

        User user = createValidUser();
        user.setId(1L);

        when(service.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].surname").value("Smith"))
                .andExpect(jsonPath("$[0].channelName").value("John's Channel"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"))
                .andExpect(jsonPath("$[0].role").value("USER_ROLE"));

        verify(service).getAllUsers();
    }

    @Test
    void shouldGetUserById() throws Exception {

        User user = createValidUser();
        user.setId(1L);

        when(service.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Smith"))
                .andExpect(jsonPath("$.channelName").value("John's Channel"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("USER_ROLE"));

        verify(service).getUserById(1L);
    }

    @Test
    void shouldUpdateUserSuccessfully() throws Exception {

        User user = createValidUser();
        User updated = createValidUser();
        updated.setId(1L);
        updated.setName("Updated John");

        when(service.updateUser(eq(1L), any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/users/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated John"))
                .andExpect(jsonPath("$.surname").value("Smith"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(authorizationService).verifyOwnership(1L);
        verify(service).updateUser(eq(1L), any(User.class));
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {

        doNothing().when(service).deleteUser(1L);

        mockMvc.perform(delete("/users/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        verify(service).deleteUser(1L);
    }

    @Test
    void shouldReturnBadRequestWhenCreateUserIsInvalid() throws Exception {

        User user = new User();

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void shouldReturnBadRequestWhenUpdateUserIsInvalid() throws Exception {

        User user = new User();

        mockMvc.perform(put("/users/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authorizationService);
        verifyNoInteractions(service);
    }

    @Test
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {

        User user = createValidUser();
        user.setEmail("not-an-email");

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    private User createValidUser() {

        User user = new User();

        user.setName("John");
        user.setSurname("Smith");
        user.setChannelName("John's Channel");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setPhoneNumber("123456789");
        user.setCity("Warsaw");
        user.setRole(UserRole.USER_ROLE);

        return user;
    }
}
