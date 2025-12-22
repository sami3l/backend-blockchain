package com.clinchain.backend.service;

import com.clinchain.backend.config.JwtTokenProvider;
import com.clinchain.backend.dto.LoginRequest;
import com.clinchain.backend.dto.LoginResponse;
import com.clinchain.backend.dto.UserDto;
import com.clinchain.backend.exception.UnauthorizedException;
import com.clinchain.backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public LoginResponse login(LoginRequest request) {
        User user = userService.findByUsername(request.getUsername());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        String token = tokenProvider.generateToken(user.getUsername(), user.getRole().name());
        UserDto userDto = userService.toDto(user);

        return LoginResponse.builder()
                .token(token)
                .user(userDto)
                .build();
    }
}
