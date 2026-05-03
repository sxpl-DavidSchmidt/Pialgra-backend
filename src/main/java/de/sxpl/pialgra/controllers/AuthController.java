package de.sxpl.pialgra.controllers;

import de.sxpl.pialgra.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = authenticationService.authenticate(loginRequest.username(), loginRequest.password());
        String token = authenticationService.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse(token, 24 * 60 * 60));
    }

    public record LoginRequest(String username, String password) {}
    public record LoginResponse(String token, long expiresIn) {}
}
