package com.influcollab.service;

import com.influcollab.entity.CollabOpportunity;
import com.influcollab.entity.CollaborationRequest;
import com.influcollab.entity.User;
import com.influcollab.enums.CollaborationRequestStatus;
import com.influcollab.exception.CollaborationRequestNotFound;
import com.influcollab.exception.UnauthorizedRequestException;
import com.influcollab.repository.CollaborationRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollaborationRequestServiceTest {

    @Mock
    private CollaborationRequestRepository collaborationRequestRepository;

    @InjectMocks
    private CollaborationRequestService service;

    @Test
    void shouldReturnSentRequests() {
        Long userId = 1L;

        CollaborationRequest request1 = new CollaborationRequest();
        CollaborationRequest request2 = new CollaborationRequest();

        List<CollaborationRequest> requests = List.of(request1, request2);

        when(collaborationRequestRepository.findBySenderId(userId))
                .thenReturn(requests);

        List<CollaborationRequest> result =
                service.getSentRequests(userId);

        assertEquals(requests, result);

        verify(collaborationRequestRepository)
                .findBySenderId(userId);

        verifyNoMoreInteractions(collaborationRequestRepository);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoSentRequests() {
        Long userId = 1L;

        when(collaborationRequestRepository.findBySenderId(userId))
                .thenReturn(List.of());

        List<CollaborationRequest> result =
                service.getSentRequests(userId);

        assertTrue(result.isEmpty());

        verify(collaborationRequestRepository)
                .findBySenderId(userId);

        verifyNoMoreInteractions(collaborationRequestRepository);
    }

    @Test
    void shouldReturnReceivedRequests() {
        Long ownerId = 5L;

        CollaborationRequest request1 = new CollaborationRequest();
        CollaborationRequest request2 = new CollaborationRequest();

        List<CollaborationRequest> requests = List.of(request1, request2);

        when(collaborationRequestRepository.findByOpportunityOwnerId(ownerId))
                .thenReturn(requests);

        List<CollaborationRequest> result =
                service.getReceivedRequests(ownerId);

        assertEquals(requests, result);

        verify(collaborationRequestRepository)
                .findByOpportunityOwnerId(ownerId);

        verifyNoMoreInteractions(collaborationRequestRepository);
    }

    @Test
    void shouldReturnEmptyListWhenOwnerHasNoReceivedRequests() {
        Long ownerId = 5L;

        when(collaborationRequestRepository.findByOpportunityOwnerId(ownerId))
                .thenReturn(List.of());

        List<CollaborationRequest> result =
                service.getReceivedRequests(ownerId);

        assertTrue(result.isEmpty());

        verify(collaborationRequestRepository)
                .findByOpportunityOwnerId(ownerId);

        verifyNoMoreInteractions(collaborationRequestRepository);
    }

    @Test
    void shouldAcceptPendingRequestWhenOwnerMatches() {
        Long requestId = 1L;
        Long ownerId = 5L;

        CollaborationRequest request =
                createPendingRequestOwnedBy(ownerId);

        when(collaborationRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        when(collaborationRequestRepository.save(request))
                .thenReturn(request);

        CollaborationRequest result =
                service.acceptRequest(requestId, ownerId);

        assertEquals(
                CollaborationRequestStatus.ACCEPTED,
                result.getStatus()
        );

        assertEquals(
                CollaborationRequestStatus.ACCEPTED,
                request.getStatus()
        );

        verify(collaborationRequestRepository)
                .findById(requestId);

        verify(collaborationRequestRepository)
                .save(argThat(saved ->
                        saved == request &&
                                saved.getStatus() ==
                                        CollaborationRequestStatus.ACCEPTED
                ));
    }

    @Test
    void shouldThrowExceptionWhenAcceptingNonExistingRequest() {
        Long requestId = 999L;
        Long ownerId = 5L;

        when(collaborationRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        assertThrows(
                CollaborationRequestNotFound.class,
                () -> service.acceptRequest(requestId, ownerId)
        );

        verify(collaborationRequestRepository)
                .findById(requestId);

        verify(collaborationRequestRepository, never())
                .save(any());

        verifyNoMoreInteractions(collaborationRequestRepository);
    }

    @Test
    void shouldThrowExceptionWhenWrongOwnerAcceptsRequest() {
        Long requestId = 1L;
        Long actualOwnerId = 5L;
        Long providedOwnerId = 99L;

        CollaborationRequest request =
                createPendingRequestOwnedBy(actualOwnerId);

        when(collaborationRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        assertThrows(
                UnauthorizedRequestException.class,
                () -> service.acceptRequest(requestId, providedOwnerId)
        );

        assertEquals(
                CollaborationRequestStatus.PENDING,
                request.getStatus()
        );

        verify(collaborationRequestRepository)
                .findById(requestId);

        verify(collaborationRequestRepository, never())
                .save(any());

        verifyNoMoreInteractions(collaborationRequestRepository);
    }

    @Test
    void shouldNotAcceptAlreadyAcceptedRequest() {
        Long requestId = 1L;
        Long ownerId = 5L;

        CollaborationRequest request =
                createRequestOwnedBy(
                        ownerId,
                        CollaborationRequestStatus.ACCEPTED
                );

        when(collaborationRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        assertThrows(
                IllegalStateException.class,
                () -> service.acceptRequest(requestId, ownerId)
        );

        assertEquals(
                CollaborationRequestStatus.ACCEPTED,
                request.getStatus()
        );

        verify(collaborationRequestRepository)
                .findById(requestId);

        verify(collaborationRequestRepository, never())
                .save(any());
    }

    @Test
    void shouldNotAcceptAlreadyRejectedRequest() {
        Long requestId = 1L;
        Long ownerId = 5L;

        CollaborationRequest request =
                createRequestOwnedBy(
                        ownerId,
                        CollaborationRequestStatus.REJECTED
                );

        when(collaborationRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        assertThrows(
                IllegalStateException.class,
                () -> service.acceptRequest(requestId, ownerId)
        );

        assertEquals(
                CollaborationRequestStatus.REJECTED,
                request.getStatus()
        );

        verify(collaborationRequestRepository)
                .findById(requestId);

        verify(collaborationRequestRepository, never())
                .save(any());
    }

    @Test
    void shouldRejectPendingRequestWhenOwnerMatches() {
        Long requestId = 1L;
        Long ownerId = 5L;

        CollaborationRequest request =
                createPendingRequestOwnedBy(ownerId);

        when(collaborationRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        when(collaborationRequestRepository.save(request))
                .thenReturn(request);

        CollaborationRequest result =
                service.rejectRequest(requestId, ownerId);

        assertEquals(
                CollaborationRequestStatus.REJECTED,
                result.getStatus()
        );

        assertEquals(
                CollaborationRequestStatus.REJECTED,
                request.getStatus()
        );

        verify(collaborationRequestRepository)
                .findById(requestId);

        verify(collaborationRequestRepository)
                .save(argThat(saved ->
                        saved == request &&
                                saved.getStatus() ==
                                        CollaborationRequestStatus.REJECTED
                ));
    }

    @Test
    void shouldThrowExceptionWhenRejectingNonExistingRequest() {
        Long requestId = 999L;
        Long ownerId = 5L;

        when(collaborationRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        assertThrows(
                CollaborationRequestNotFound.class,
                () -> service.rejectRequest(requestId, ownerId)
        );

        verify(collaborationRequestRepository)
                .findById(requestId);

        verify(collaborationRequestRepository, never())
                .save(any());

        verifyNoMoreInteractions(collaborationRequestRepository);
    }

    @Test
    void shouldThrowExceptionWhenWrongOwnerRejectsRequest() {
        Long requestId = 1L;
        Long actualOwnerId = 5L;
        Long providedOwnerId = 99L;

        CollaborationRequest request =
                createPendingRequestOwnedBy(actualOwnerId);

        when(collaborationRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        assertThrows(
                UnauthorizedRequestException.class,
                () -> service.rejectRequest(requestId, providedOwnerId)
        );

        assertEquals(
                CollaborationRequestStatus.PENDING,
                request.getStatus()
        );

        verify(collaborationRequestRepository)
                .findById(requestId);

        verify(collaborationRequestRepository, never())
                .save(any());

        verifyNoMoreInteractions(collaborationRequestRepository);
    }

    @Test
    void shouldNotRejectAlreadyAcceptedRequest() {
        Long requestId = 1L;
        Long ownerId = 5L;

        CollaborationRequest request =
                createRequestOwnedBy(
                        ownerId,
                        CollaborationRequestStatus.ACCEPTED
                );

        when(collaborationRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        assertThrows(
                IllegalStateException.class,
                () -> service.rejectRequest(requestId, ownerId)
        );

        assertEquals(
                CollaborationRequestStatus.ACCEPTED,
                request.getStatus()
        );

        verify(collaborationRequestRepository)
                .findById(requestId);

        verify(collaborationRequestRepository, never())
                .save(any());
    }

    @Test
    void shouldNotRejectAlreadyRejectedRequest() {
        Long requestId = 1L;
        Long ownerId = 5L;

        CollaborationRequest request =
                createRequestOwnedBy(
                        ownerId,
                        CollaborationRequestStatus.REJECTED
                );

        when(collaborationRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        assertThrows(
                IllegalStateException.class,
                () -> service.rejectRequest(requestId, ownerId)
        );

        assertEquals(
                CollaborationRequestStatus.REJECTED,
                request.getStatus()
        );

        verify(collaborationRequestRepository)
                .findById(requestId);

        verify(collaborationRequestRepository, never())
                .save(any());
    }

    private CollaborationRequest createPendingRequestOwnedBy(Long ownerId) {
        return createRequestOwnedBy(
                ownerId,
                CollaborationRequestStatus.PENDING
        );
    }

    private CollaborationRequest createRequestOwnedBy(
            Long ownerId,
            CollaborationRequestStatus status
    ) {
        User owner = new User();
        owner.setId(ownerId);

        CollabOpportunity opportunity =
                new CollabOpportunity();

        opportunity.setUser(owner);

        CollaborationRequest request =
                new CollaborationRequest();

        request.setOpportunity(opportunity);
        request.setStatus(status);

        return request;
    }
}