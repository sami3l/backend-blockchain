package com.clinchain.backend.controller;

import com.clinchain.backend.dto.LoginRequest;
import com.clinchain.backend.dto.LoginResponse;
import com.clinchain.backend.dto.UserDto;
import com.clinchain.backend.service.AuthService;
import com.clinchain.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication & user session APIs")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(
            summary = "Login",
            description = "Authenticate user and return JWT token"
    )
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get current user",
            description = "Returns information about the authenticated user"
    )
    @SecurityRequirement(name = "bearerAuth") // 🔐 requires JWT
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        UserDto userDto = userService.toDto(userService.findByUsername(username));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout",
            description = "Client-side logout (JWT is stateless)"
    )
    @SecurityRequirement(name = "bearerAuth") // 🔐 requires JWT
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}