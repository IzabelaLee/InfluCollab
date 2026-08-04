package com.influcollab.controller;

import com.influcollab.dto.LoginRequest;
import com.influcollab.dto.LoginResponse;
import com.influcollab.dto.UserSummaryDTO;
import com.influcollab.entity.User;
import com.influcollab.service.AuthenticationService;
import com.influcollab.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public AuthController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        User user = authenticationService.authenticate(request);

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());

        UserSummaryDTO userSummary = new UserSummaryDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getChannelName()
        );

        LoginResponse response = new LoginResponse(token, userSummary);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

