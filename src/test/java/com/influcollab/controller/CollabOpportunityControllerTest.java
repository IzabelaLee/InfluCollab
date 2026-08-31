package com.influcollab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.influcollab.dto.CollabOpportunityDTO;
import com.influcollab.dto.CollaborationRequestDTO;
import com.influcollab.dto.CreateCollaborationRequestDTO;
import com.influcollab.security.CustomUserDetailsService;
import com.influcollab.service.CollabOpportunityService;
import com.influcollab.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CollabOpportunityController.class)
@AutoConfigureMockMvc(addFilters = false)
class CollabOpportunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CollabOpportunityService service;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    void shouldGetAllCollabOpportunities() throws Exception {

        CollabOpportunityDTO opportunity = new CollabOpportunityDTO();
        opportunity.setId(1L);
        opportunity.setTitle("Summer Campaign");
        opportunity.setDescription("Looking for a content creator");
        opportunity.setCity("Warsaw");

        Page<CollabOpportunityDTO> page = new PageImpl<>(List.of(opportunity));

        when(service.getAllCollabOpportunities(isNull(), isNull(), isNull(), isNull(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/opportunities").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(service).getAllCollabOpportunities(isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
    }


    @Test
    void shouldGetAllCollabOpportunitiesWithFilters() throws Exception {

        CollabOpportunityDTO opportunity = new CollabOpportunityDTO();
        opportunity.setId(1L);
        opportunity.setTitle("Summer Campaign");
        opportunity.setDescription("Looking for a content creator");
        opportunity.setCity("Warsaw");

        Page<CollabOpportunityDTO> page = new PageImpl<>(List.of(opportunity));

        when(service.getAllCollabOpportunities(eq("Warsaw"), any(), any(), eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/opportunities")
                        .param("city", "Warsaw")
                        .param("from", "2026-08-01")
                        .param("to", "2026-08-31")
                        .param("ownerId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));

        verify(service).getAllCollabOpportunities(
                eq("Warsaw"),
                eq(java.time.LocalDate.of(2026, 8, 1)),
                eq(java.time.LocalDate.of(2026, 8, 31)),
                eq(1L),
                any(Pageable.class)
        );
    }

    @Test
    void shouldGetCollabOpportunityById() throws Exception {

        CollabOpportunityDTO opportunity = new CollabOpportunityDTO();
        opportunity.setId(1L);
        opportunity.setTitle("Summer Campaign");
        opportunity.setDescription("Looking for a content creator");
        opportunity.setCity("Warsaw");

        when(service.getCollabOpportunityById(1L)).thenReturn(opportunity);

        mockMvc.perform(get("/opportunities/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        verify(service).getCollabOpportunityById(1L);
    }

    @Test
    void shouldCreateCollaborationRequestSuccessfully() throws Exception {

        CreateCollaborationRequestDTO requestDTO = new CreateCollaborationRequestDTO();

        requestDTO.setSenderId(2L);
        requestDTO.setMessage("I would like to collaborate with you.");

        CollaborationRequestDTO created = new CollaborationRequestDTO();

        when(service.createCollaborationRequest(eq(1L), any(CreateCollaborationRequestDTO.class))).thenReturn(created);

        mockMvc.perform(
                        post("/opportunities/1/requests")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO))
                )
                .andExpect(status().isCreated());

        verify(service).createCollaborationRequest(eq(1L), any(CreateCollaborationRequestDTO.class));
    }

    @Test
    void shouldReturnBadRequestWhenCollaborationRequestIsInvalid() throws Exception {

        CreateCollaborationRequestDTO requestDTO = new CreateCollaborationRequestDTO();

        requestDTO.setSenderId(null);
        requestDTO.setMessage("");

        mockMvc.perform(
                        post("/opportunities/1/requests")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO))
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}