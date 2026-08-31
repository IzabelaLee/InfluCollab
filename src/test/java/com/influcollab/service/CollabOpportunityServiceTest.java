package com.influcollab.service;

import com.influcollab.dto.*;
import com.influcollab.entity.CollabOpportunity;
import com.influcollab.entity.CollaborationRequest;
import com.influcollab.entity.User;
import com.influcollab.enums.CollaborationRequestStatus;
import com.influcollab.exception.CollabNotFoundException;
import com.influcollab.exception.InvalidCollaborationRequestException;
import com.influcollab.exception.UserNotFoundException;
import com.influcollab.repository.CollabOpportunityRepository;
import com.influcollab.repository.CollaborationRequestRepository;
import com.influcollab.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollabOpportunityServiceTest {

    @Mock
    private CollabOpportunityRepository collabOpportunityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CollaborationRequestRepository collaborationRequestRepository;

    private CollabOpportunityService service;

    @BeforeEach
    void setUp() {

        CollabOpportunityMapper collabOpportunityMapper = new CollabOpportunityMapper();
        CollaborationRequestMapper collaborationRequestMapper = new CollaborationRequestMapper();

        service = new CollabOpportunityService(
                collabOpportunityRepository,
                userRepository,
                collaborationRequestRepository,
                collabOpportunityMapper,
                collaborationRequestMapper
        );
    }

    @Test
    void shouldReturnCollabOpportunityById() {
        Long opportunityId = 1L;

        User owner = createUser(5L, "John", "john@example.com");
        CollabOpportunity opportunity = createOpportunity(opportunityId, owner, "Photography Campaign");

        when(collabOpportunityRepository.findById(opportunityId)).thenReturn(Optional.of(opportunity));

        CollabOpportunityDTO result = service.getCollabOpportunityById(opportunityId);

        assertNotNull(result);
        assertEquals(opportunityId, result.getId());
        assertEquals("Photography Campaign", result.getTitle());
        assertEquals("John", result.getCreator().getName());
        assertEquals("john@example.com", result.getCreator().getEmail());

        verify(collabOpportunityRepository).findById(opportunityId);

        verifyNoMoreInteractions(collabOpportunityRepository);
    }

    @Test
    void shouldThrowExceptionWhenCollabOpportunityDoesNotExist() {
        Long opportunityId = 999L;

        when(collabOpportunityRepository.findById(opportunityId)).thenReturn(Optional.empty());

        assertThrows(CollabNotFoundException.class, () -> service.getCollabOpportunityById(opportunityId));

        verify(collabOpportunityRepository).findById(opportunityId);
        verifyNoMoreInteractions(collabOpportunityRepository);
    }

    @Test
    void shouldReturnAllCollabOpportunitiesByUser() {
        Long userId = 5L;

        User owner = createUser(userId, "John", "john@example.com");

        CollabOpportunity opportunity1 = createOpportunity(1L, owner, "Campaign 1");
        CollabOpportunity opportunity2 = createOpportunity(2L, owner, "Campaign 2");

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(collabOpportunityRepository.findByOwnerId(userId)).thenReturn(List.of(opportunity1, opportunity2));

        List<CollabOpportunityDTO> result = service.getAllCollabOpportunitiesByUser(userId);

        assertEquals(2, result.size());
        assertEquals("Campaign 1", result.get(0).getTitle());
        assertEquals("Campaign 2", result.get(1).getTitle());

        verify(userRepository).findById(userId);
        verify(collabOpportunityRepository).findByOwnerId(userId);
        verifyNoMoreInteractions(userRepository, collabOpportunityRepository);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoOpportunities() {
        Long userId = 5L;

        User owner = createUser(userId, "John", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(collabOpportunityRepository.findByOwnerId(userId)).thenReturn(List.of());

        List<CollabOpportunityDTO> result = service.getAllCollabOpportunitiesByUser(userId);

        assertTrue(result.isEmpty());

        verify(userRepository).findById(userId);
        verify(collabOpportunityRepository).findByOwnerId(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.getAllCollabOpportunitiesByUser(userId));

        verify(userRepository).findById(userId);
        verify(collabOpportunityRepository, never()).findByOwnerId(anyLong());
    }

    @Test
    void shouldReturnOpportunityWhenUserOwnsIt() {
        Long userId = 5L;
        Long opportunityId = 10L;

        User owner = createUser(userId, "John", "john@example.com");
        CollabOpportunity opportunity = createOpportunity(opportunityId, owner, "Campaign");

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(collabOpportunityRepository.findByIdAndOwnerId(opportunityId, userId)).thenReturn(Optional.of(opportunity));

        CollabOpportunityDTO result = service.getCollabOpportunityByIdAndUserId(userId, opportunityId);

        assertNotNull(result);
        assertEquals(opportunityId, result.getId());
        assertEquals("Campaign", result.getTitle());

        verify(userRepository).findById(userId);
        verify(collabOpportunityRepository).findByIdAndOwnerId(opportunityId, userId);
    }

    @Test
    void shouldThrowExceptionWhenGettingOpportunityForNonExistingUser() {
        Long userId = 999L;
        Long opportunityId = 10L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.getCollabOpportunityByIdAndUserId(userId, opportunityId));

        verify(userRepository).findById(userId);
        verify(collabOpportunityRepository, never()).findByIdAndOwnerId(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenOpportunityDoesNotBelongToUser() {
        Long userId = 5L;
        Long opportunityId = 10L;

        User owner = createUser(userId, "John", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(collabOpportunityRepository.findByIdAndOwnerId(opportunityId, userId)).thenReturn(Optional.empty());

        assertThrows(CollabNotFoundException.class, () -> service.getCollabOpportunityByIdAndUserId(userId, opportunityId));

        verify(userRepository).findById(userId);
        verify(collabOpportunityRepository).findByIdAndOwnerId(opportunityId, userId);
    }

    @Test
    void shouldCreateCollabOpportunity() {
        Long ownerId = 5L;

        User owner = createUser(ownerId, "John", "john@example.com");
        CollabOpportunity opportunity = createOpportunity(null, null, "New Campaign");
        CollabOpportunity savedOpportunity = createOpportunity(10L, owner, "New Campaign");

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(collabOpportunityRepository.save(opportunity)).thenReturn(savedOpportunity);

        CollabOpportunityDTO result = service.createCollabOpportunity(opportunity, ownerId);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("New Campaign", result.getTitle());

        assertEquals(owner, opportunity.getUser());

        verify(userRepository).findById(ownerId);
        verify(collabOpportunityRepository).save(opportunity);
    }

    @Test
    void shouldThrowExceptionWhenCreatingOpportunityForNonExistingUser() {
        Long ownerId = 999L;

        CollabOpportunity opportunity = createOpportunity(null, null, "Campaign");

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.createCollabOpportunity(opportunity, ownerId));

        verify(userRepository).findById(ownerId);
        verify(collabOpportunityRepository, never()).save(any());
    }

    @Test
    void shouldUpdateCollabOpportunity() {
        Long userId = 5L;
        Long opportunityId = 10L;

        User owner = createUser(userId, "John", "john@example.com");
        CollabOpportunity existing = createOpportunity(opportunityId, owner, "Old title");
        CollabOpportunity updatedData = createOpportunity(null, null, "New title");

        updatedData.setCity("Warsaw");
        updatedData.setStartDate(LocalDate.of(2026, 9, 1));
        updatedData.setEndDate(LocalDate.of(2026, 9, 10));
        updatedData.setDescription("New description");

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(collabOpportunityRepository.findById(opportunityId)).thenReturn(Optional.of(existing));
        when(collabOpportunityRepository.save(existing)).thenReturn(existing);

        CollabOpportunityDTO result = service.updateCollabOpportunity(userId, opportunityId, updatedData);

        assertEquals("New title", existing.getTitle());
        assertEquals("Warsaw", existing.getCity());
        assertEquals(LocalDate.of(2026, 9, 1), existing.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 10), existing.getEndDate());
        assertEquals("New description", existing.getDescription());
        assertEquals("New title", result.getTitle());

        verify(userRepository).findById(userId);
        verify(collabOpportunityRepository).findById(opportunityId);
        verify(collabOpportunityRepository).save(existing);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingForNonExistingUser() {
        Long userId = 999L;
        Long opportunityId = 10L;

        CollabOpportunity updatedData = createOpportunity(null, null, "New title");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.updateCollabOpportunity(userId, opportunityId, updatedData));

        verify(userRepository).findById(userId);
        verify(collabOpportunityRepository, never()).findById(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingOpportunity() {
        Long userId = 5L;
        Long opportunityId = 999L;

        User owner = createUser(userId, "John", "john@example.com");
        CollabOpportunity updatedData = createOpportunity(null, null, "New title");

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(collabOpportunityRepository.findById(opportunityId)).thenReturn(Optional.empty());

        assertThrows(CollabNotFoundException.class, () -> service.updateCollabOpportunity(userId, opportunityId, updatedData));

        verify(userRepository).findById(userId);
        verify(collabOpportunityRepository).findById(opportunityId);
        verify(collabOpportunityRepository, never()).save(any());
    }

    @Test
    void shouldUpdateOnlyProvidedOpportunityDetails() {
        Long userId = 5L;
        Long opportunityId = 10L;

        User owner = createUser(userId, "John", "john@example.com");

        CollabOpportunity opportunity = createOpportunity(opportunityId, owner, "Old title");

        opportunity.setCity("Krakow");
        opportunity.setStartDate(LocalDate.of(2026, 9, 1));
        opportunity.setDescription("Old description");

        UpdateCollabOpportunityDTO updateDTO = new UpdateCollabOpportunityDTO();

        updateDTO.setTitle("New title");
        updateDTO.setCity("Warsaw");

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(collabOpportunityRepository.findByIdAndOwnerId(opportunityId, userId)).thenReturn(Optional.of(opportunity));
        when(collabOpportunityRepository.save(opportunity)).thenReturn(opportunity);

        CollabOpportunityDTO result = service.updateCollabOpportunityDetail(userId, opportunityId, updateDTO);

        assertEquals("New title", opportunity.getTitle());
        assertEquals("Warsaw", opportunity.getCity());

        assertEquals(LocalDate.of(2026, 9, 1), opportunity.getStartDate());
        assertEquals("Old description", opportunity.getDescription());
        assertEquals("New title", result.getTitle());

        verify(collabOpportunityRepository).save(opportunity);
    }

    @Test
    void shouldIgnoreBlankTitleAndCity() {
        Long userId = 5L;
        Long opportunityId = 10L;

        User owner = createUser(userId, "John", "john@example.com");
        CollabOpportunity opportunity = createOpportunity(opportunityId, owner, "Original title");

        opportunity.setCity("Krakow");

        UpdateCollabOpportunityDTO updateDTO = new UpdateCollabOpportunityDTO();

        updateDTO.setTitle("   ");
        updateDTO.setCity("");

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(collabOpportunityRepository.findByIdAndOwnerId(opportunityId, userId)).thenReturn(Optional.of(opportunity));
        when(collabOpportunityRepository.save(opportunity)).thenReturn(opportunity);

        service.updateCollabOpportunityDetail(userId, opportunityId, updateDTO);

        assertEquals("Original title", opportunity.getTitle());
        assertEquals("Krakow", opportunity.getCity());

        verify(collabOpportunityRepository).save(opportunity);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingDetailsForNonExistingUser() {
        Long userId = 999L;
        Long opportunityId = 10L;

        UpdateCollabOpportunityDTO updateDTO = new UpdateCollabOpportunityDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.updateCollabOpportunityDetail(userId, opportunityId, updateDTO));

        verify(userRepository).findById(userId);
        verify(collabOpportunityRepository, never()).findByIdAndOwnerId(anyLong(), anyLong());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingDetailsForNonExistingOpportunity() {
        Long userId = 5L;
        Long opportunityId = 999L;

        User owner = createUser(userId, "John", "john@example.com");

        UpdateCollabOpportunityDTO updateDTO = new UpdateCollabOpportunityDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(collabOpportunityRepository.findByIdAndOwnerId(opportunityId, userId)).thenReturn(Optional.empty());

        assertThrows(CollabNotFoundException.class, () -> service.updateCollabOpportunityDetail(userId, opportunityId, updateDTO));

        verify(collabOpportunityRepository).findByIdAndOwnerId(opportunityId, userId);
        verify(collabOpportunityRepository, never()).save(any());
    }

    @Test
    void shouldDeleteCollabOpportunity() {
        Long userId = 5L;
        Long opportunityId = 10L;

        User owner = createUser(userId, "John", "john@example.com");

        CollabOpportunity opportunity = createOpportunity(opportunityId, owner, "Campaign");

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(collabOpportunityRepository.findByIdAndOwnerId(opportunityId, userId)).thenReturn(Optional.of(opportunity));

        service.deleteCollabOpportunity(userId, opportunityId);

        verify(userRepository).findById(userId);
        verify(collabOpportunityRepository).findByIdAndOwnerId(opportunityId, userId);
        verify(collabOpportunityRepository).delete(opportunity);
    }

    @Test
    void shouldThrowExceptionWhenDeletingForNonExistingUser() {
        Long userId = 999L;
        Long opportunityId = 10L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.deleteCollabOpportunity(userId, opportunityId));

        verify(userRepository).findById(userId);
        verify(collabOpportunityRepository, never()).findByIdAndOwnerId(anyLong(), anyLong());
        verify(collabOpportunityRepository, never()).delete(any(CollabOpportunity.class));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingOpportunity() {
        Long userId = 5L;
        Long opportunityId = 999L;

        User owner = createUser(userId, "John", "john@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(collabOpportunityRepository.findByIdAndOwnerId(opportunityId, userId)).thenReturn(Optional.empty());

        assertThrows(CollabNotFoundException.class, () -> service.deleteCollabOpportunity(userId, opportunityId));

        verify(collabOpportunityRepository).findByIdAndOwnerId(opportunityId, userId);
        verify(collabOpportunityRepository, never()).delete(any(CollabOpportunity.class));
    }

    @Test
    void shouldCreateCollaborationRequest() {
        Long opportunityId = 10L;
        Long senderId = 20L;

        User owner = createUser(5L, "Owner", "owner@example.com");
        User sender = createUser(senderId, "Sender", "sender@example.com");

        CollabOpportunity opportunity = createOpportunity(opportunityId, owner, "Campaign");

        CreateCollaborationRequestDTO requestDTO = new CreateCollaborationRequestDTO();

        requestDTO.setSenderId(senderId);
        requestDTO.setMessage("I would love to collaborate!");

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(collabOpportunityRepository.findById(opportunityId)).thenReturn(Optional.of(opportunity));
        when(collaborationRequestRepository.save(any(CollaborationRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CollaborationRequestDTO result = service.createCollaborationRequest(opportunityId, requestDTO);

        assertNotNull(result);
        assertEquals(senderId, result.getSenderId());
        assertEquals(opportunityId, result.getOpportunityId());
        assertEquals("I would love to collaborate!", result.getMessage());
        assertEquals(CollaborationRequestStatus.PENDING, result.getStatus());

        verify(userRepository).findById(senderId);
        verify(collabOpportunityRepository).findById(opportunityId);
        verify(collaborationRequestRepository).save(any(CollaborationRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenSenderDoesNotExist() {
        Long opportunityId = 10L;
        Long senderId = 999L;

        CreateCollaborationRequestDTO requestDTO = new CreateCollaborationRequestDTO();

        requestDTO.setSenderId(senderId);
        requestDTO.setMessage("Hello");

        when(userRepository.findById(senderId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.createCollaborationRequest(opportunityId, requestDTO));

        verify(userRepository).findById(senderId);
        verify(collabOpportunityRepository, never()).findById(anyLong());
        verify(collaborationRequestRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenOpportunityDoesNotExistForCollaborationRequest() {
        Long opportunityId = 999L;
        Long senderId = 20L;

        User sender = createUser(senderId, "Sender", "sender@example.com");

        CreateCollaborationRequestDTO requestDTO = new CreateCollaborationRequestDTO();

        requestDTO.setSenderId(senderId);
        requestDTO.setMessage("Hello");

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(collabOpportunityRepository.findById(opportunityId)).thenReturn(Optional.empty());

        assertThrows(CollabNotFoundException.class, () -> service.createCollaborationRequest(opportunityId, requestDTO));

        verify(userRepository).findById(senderId);
        verify(collabOpportunityRepository).findById(opportunityId);
        verify(collaborationRequestRepository, never()).save(any());
    }

    @Test
    void shouldNotAllowOwnerToSendCollaborationRequestToOwnOpportunity() {
        Long opportunityId = 10L;
        Long ownerId = 5L;

        User owner = createUser(ownerId, "Owner", "owner@example.com");
        CollabOpportunity opportunity = createOpportunity(opportunityId, owner, "Campaign");

        CreateCollaborationRequestDTO requestDTO = new CreateCollaborationRequestDTO();

        requestDTO.setSenderId(ownerId);
        requestDTO.setMessage("I want to collaborate with myself");

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(collabOpportunityRepository.findById(opportunityId)).thenReturn(Optional.of(opportunity));

        assertThrows(InvalidCollaborationRequestException.class, () -> service.createCollaborationRequest(opportunityId, requestDTO));

        verify(userRepository).findById(ownerId);
        verify(collabOpportunityRepository).findById(opportunityId);
        verify(collaborationRequestRepository, never())
                .save(any());
    }

    private User createUser(Long id, String name, String email) {
        User user = new User();

        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private CollabOpportunity createOpportunity(Long id, User owner, String title) {
        CollabOpportunity opportunity = new CollabOpportunity();

        opportunity.setId(id);
        opportunity.setUser(owner);
        opportunity.setTitle(title);
        opportunity.setCity("Warsaw");
        opportunity.setStartDate(LocalDate.of(2026, 9, 1));
        opportunity.setEndDate(LocalDate.of(2026, 9, 10));
        opportunity.setDescription("Test collaboration opportunity");

        return opportunity;
    }
}