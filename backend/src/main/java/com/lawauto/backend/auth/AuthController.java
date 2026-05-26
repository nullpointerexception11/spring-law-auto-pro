package com.lawauto.backend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthService.LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request.email(), request.password(), request.orgName());
    }

    public record LoginRequest(String email, String password, String orgName) {
    }
}
