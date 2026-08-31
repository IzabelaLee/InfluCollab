package com.influcollab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.influcollab.dto.LoginRequest;
import com.influcollab.entity.User;
import com.influcollab.enums.UserRole;
import com.influcollab.service.AuthenticationService;
import com.influcollab.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtService jwtService;

    @Test
    void shouldLoginSuccessfully() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("test@example.com");
        user.setChannelName("John's Channel");
        user.setRole(UserRole.USER_ROLE);

        String token = "test-jwt-token";

        when(authenticationService.authenticate(any(LoginRequest.class))).thenReturn(user);
        when(jwtService.generateToken(user.getId(), user.getEmail(), user.getRole())).thenReturn(token);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.name").value("John"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.channelName")
                        .value("John's Channel"));

        verify(authenticationService).authenticate(any(LoginRequest.class));
        verify(jwtService).generateToken(user.getId(), user.getEmail(), user.getRole());
    }

    @Test
    void shouldReturnBadRequestWhenLoginRequestIsInvalid() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("");
        request.setPassword("");

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authenticationService);
        verifyNoInteractions(jwtService);
    }
}
