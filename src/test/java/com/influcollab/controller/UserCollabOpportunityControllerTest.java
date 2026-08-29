package com.influcollab.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.influcollab.dto.CollabOpportunityDTO;
import com.influcollab.dto.UpdateCollabOpportunityDTO;
import com.influcollab.entity.CollabOpportunity;
import com.influcollab.security.CustomUserDetailsService;
import com.influcollab.service.AuthorizationService;
import com.influcollab.service.CollabOpportunityService;
import com.influcollab.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserCollabOpportunityController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserCollabOpportunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CollabOpportunityService service;

    @MockBean
    private AuthorizationService authorizationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    void shouldGetUserAllOpportunities() throws Exception {

        CollabOpportunityDTO opportunity = new CollabOpportunityDTO();

        List<CollabOpportunityDTO> opportunities = List.of(opportunity);

        when(service.getAllCollabOpportunitiesByUser(1L)).thenReturn(opportunities);

        mockMvc.perform(get("/users/1/opportunities").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(service).getAllCollabOpportunitiesByUser(1L);
    }

    @Test
    void shouldGetUserOpportunityById() throws Exception {

        CollabOpportunityDTO opportunity = new CollabOpportunityDTO();

        when(service.getCollabOpportunityByIdAndUserId(1L, 10L)).thenReturn(opportunity);

        mockMvc.perform(get("/users/1/opportunities/10").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        verify(service).getCollabOpportunityByIdAndUserId(1L, 10L);
    }

    @Test
    void shouldCreateCollabOpportunitySuccessfully() throws Exception {

        CollabOpportunity opportunity = new CollabOpportunity();

        opportunity.setTitle("Summer Campaign");
        opportunity.setCity("Warsaw");
        opportunity.setStartDate(LocalDate.of(2026, 9, 1));
        opportunity.setEndDate(LocalDate.of(2026, 9, 30));

        CollabOpportunityDTO created = new CollabOpportunityDTO();

        when(service.createCollabOpportunity(any(CollabOpportunity.class), eq(1L))).thenReturn(created);

        mockMvc.perform(
                        post("/users/1/opportunities")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(opportunity))
                )
                .andExpect(status().isCreated());

        verify(authorizationService).verifyOwnership(1L);
        verify(service).createCollabOpportunity(any(CollabOpportunity.class), eq(1L));
    }

    @Test
    void shouldReturnBadRequestWhenCreateOpportunityIsInvalid() throws Exception {

        CollabOpportunity opportunity = new CollabOpportunity();

        mockMvc.perform(
                        post("/users/1/opportunities")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(opportunity))
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authorizationService);
        verifyNoInteractions(service);
    }

    @Test
    void shouldUpdateCollabOpportunitySuccessfully() throws Exception {

        CollabOpportunity opportunity = new CollabOpportunity();

        opportunity.setTitle("Updated Summer Campaign");
        opportunity.setCity("Warsaw");
        opportunity.setStartDate(LocalDate.of(2026, 9, 1));
        opportunity.setEndDate(LocalDate.of(2026, 9, 30));

        CollabOpportunityDTO updated = new CollabOpportunityDTO();

        when(service.updateCollabOpportunity(eq(1L), eq(10L), any(CollabOpportunity.class))).thenReturn(updated);

        mockMvc.perform(
                        put("/users/1/opportunities/10")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(opportunity))
                )
                .andExpect(status().isOk());

        verify(authorizationService).verifyOwnership(1L);
        verify(service).updateCollabOpportunity(eq(1L), eq(10L), any(CollabOpportunity.class));
    }

    @Test
    void shouldReturnBadRequestWhenUpdateOpportunityIsInvalid() throws Exception {

        CollabOpportunity opportunity = new CollabOpportunity();

        mockMvc.perform(
                        put("/users/1/opportunities/10")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(opportunity))
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authorizationService);
        verifyNoInteractions(service);
    }


    @Test
    void shouldPatchCollabOpportunitySuccessfully() throws Exception {

        UpdateCollabOpportunityDTO updateDTO = new UpdateCollabOpportunityDTO();
        CollabOpportunityDTO updated = new CollabOpportunityDTO();

        when(service.updateCollabOpportunityDetail(eq(1L), eq(10L), any(UpdateCollabOpportunityDTO.class))).thenReturn(updated);

        mockMvc.perform(
                        patch("/users/1/opportunities/10")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDTO))
                )
                .andExpect(status().isOk());

        verify(authorizationService).verifyOwnership(1L);
        verify(service).updateCollabOpportunityDetail(eq(1L), eq(10L), any(UpdateCollabOpportunityDTO.class));
    }

    @Test
    void shouldDeleteCollabOpportunitySuccessfully() throws Exception {

        doNothing().when(service).deleteCollabOpportunity(1L, 10L);

        mockMvc.perform(
                        delete("/users/1/opportunities/10")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        verify(service).deleteCollabOpportunity(1L, 10L);
    }
}
