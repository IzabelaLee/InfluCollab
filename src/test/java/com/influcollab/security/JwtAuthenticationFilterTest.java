package com.influcollab.security;

import com.influcollab.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private UserDetails userDetails;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsMissing() throws Exception {

        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldContinueFilterChainWhenAuthorizationHeaderIsNotBearer() throws Exception {

        when(request.getHeader("Authorization")).thenReturn("Basic some-token");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(userDetailsService);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldContinueFilterChainWhenTokenIsInvalid() throws Exception {

        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtService.isTokenValid("invalid-token")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtService).isTokenValid("invalid-token");
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractEmail(anyString());
        verify(jwtService, never()).extractRole(anyString());
        verifyNoInteractions(userDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValid() throws Exception {

        String token = "valid-token";
        String email = "john@example.com";
        String role = "USER_ROLE";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractEmail(token)).thenReturn(email);
        when(jwtService.extractRole(token)).thenReturn(role);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtService).isTokenValid(token);
        verify(jwtService).extractEmail(token);
        verify(jwtService).extractRole(token);
        verify(userDetailsService).loadUserByUsername(email);
        verify(filterChain).doFilter(request, response);

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, authentication);
        assertSame(userDetails, authentication.getPrincipal());
        assertTrue(authentication.isAuthenticated());
    }

    @Test
    void shouldNotAuthenticateWhenEmailIsNull() throws Exception {

        String token = "valid-token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractEmail(token)).thenReturn(null);
        when(jwtService.extractRole(token)).thenReturn("USER_ROLE");

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtService).isTokenValid(token);
        verify(jwtService).extractEmail(token);
        verify(jwtService).extractRole(token);
        verifyNoInteractions(userDetailsService);
        verify(filterChain).doFilter(request, response);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldNotOverwriteExistingAuthentication() throws Exception {

        UsernamePasswordAuthenticationToken existingAuthentication =
                new UsernamePasswordAuthenticationToken(
                        "existing-user",
                        null,
                        java.util.Collections.emptyList()
                );

        SecurityContextHolder.getContext().setAuthentication(existingAuthentication);

        String token = "valid-token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractEmail(token)).thenReturn("john@example.com");
        when(jwtService.extractRole(token)).thenReturn("USER_ROLE");

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtService).isTokenValid(token);
        verify(jwtService).extractEmail(token);
        verify(jwtService).extractRole(token);
        verifyNoInteractions(userDetailsService);
        verify(filterChain).doFilter(request, response);

        assertSame(existingAuthentication, SecurityContextHolder.getContext().getAuthentication());
    }
}