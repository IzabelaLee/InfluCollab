package com.influcollab.controller;

import com.influcollab.entity.CollaborationRequest;
import com.influcollab.entity.User;
import com.influcollab.enums.CollaborationRequestStatus;
import com.influcollab.security.CustomUserDetailsService;
import com.influcollab.service.CollaborationRequestService;
import com.influcollab.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CollaborationRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class CollaborationRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CollaborationRequestService service;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    void shouldGetSentRequests() throws Exception {

        User sender = new User();
        sender.setId(2L);
        sender.setName("John");
        sender.setEmail("john@example.com");

        CollaborationRequest request = new CollaborationRequest();
        request.setSender(sender);
        request.setMessage("I would like to collaborate with you.");
        request.setStatus(CollaborationRequestStatus.PENDING);

        when(service.getSentRequests(2L)).thenReturn(List.of(request));

        mockMvc.perform(get("/requests/sent").param("userId", "2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].message").value("I would like to collaborate with you."))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].sender.id").value(2))
                .andExpect(jsonPath("$[0].sender.name").value("John"))
                .andExpect(jsonPath("$[0].sender.email").value("john@example.com"));

        verify(service).getSentRequests(2L);
    }


    @Test
    void shouldGetReceivedRequests() throws Exception {

        User sender = new User();
        sender.setId(2L);
        sender.setName("John");
        sender.setEmail("john@example.com");

        CollaborationRequest request = new CollaborationRequest();
        request.setSender(sender);
        request.setMessage("I would like to collaborate with you.");
        request.setStatus(CollaborationRequestStatus.PENDING);

        when(service.getReceivedRequests(1L)).thenReturn(List.of(request));

        mockMvc.perform(get("/requests/received").param("ownerId", "1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].message").value("I would like to collaborate with you."))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].sender.id").value(2))
                .andExpect(jsonPath("$[0].sender.name").value("John"))
                .andExpect(jsonPath("$[0].sender.email").value("john@example.com"));

        verify(service).getReceivedRequests(1L);
    }


    @Test
    void shouldAcceptRequest() throws Exception {

        User sender = new User();
        sender.setId(2L);
        sender.setName("John");

        CollaborationRequest request = new CollaborationRequest();
        request.setSender(sender);
        request.setMessage("I would like to collaborate with you.");
        request.setStatus(CollaborationRequestStatus.ACCEPTED);

        when(service.acceptRequest(1L, 2L)).thenReturn(request);

        mockMvc.perform(put("/requests/1/accept").param("ownerId", "2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("I would like to collaborate with you."))
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.sender.id").value(2))
                .andExpect(jsonPath("$.sender.name").value("John"));

        verify(service).acceptRequest(1L, 2L);
    }

    @Test
    void shouldRejectRequest() throws Exception {

        User sender = new User();
        sender.setId(2L);
        sender.setName("John");

        CollaborationRequest request = new CollaborationRequest();
        request.setSender(sender);
        request.setMessage("I would like to collaborate with you.");
        request.setStatus(CollaborationRequestStatus.REJECTED);

        when(service.rejectRequest(1L, 2L)).thenReturn(request);

        mockMvc.perform(put("/requests/1/reject").param("ownerId", "2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("I would like to collaborate with you."))
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.sender.id").value(2))
                .andExpect(jsonPath("$.sender.name").value("John"));

        verify(service).rejectRequest(1L, 2L);
    }

    @Test
    void shouldReturnBadRequestWhenUserIdIsMissing() throws Exception {

        mockMvc.perform(get("/requests/sent").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void shouldReturnBadRequestWhenOwnerIdIsMissing() throws Exception {

        mockMvc.perform(get("/requests/received").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void shouldReturnBadRequestWhenAcceptOwnerIdIsMissing()
            throws Exception {

        mockMvc.perform(put("/requests/1/accept").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void shouldReturnBadRequestWhenRejectOwnerIdIsMissing()
            throws Exception {

        mockMvc.perform(put("/requests/1/reject").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}