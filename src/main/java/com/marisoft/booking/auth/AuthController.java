package com.marisoft.booking.auth;

import com.marisoft.booking.auth.AuthDto.CurrentUserResponse;
import com.marisoft.booking.auth.AuthDto.LoginRequest;
import com.marisoft.booking.auth.AuthDto.LoginResponse;
import com.marisoft.booking.auth.AuthDto.RefreshTokenRequest;
import com.marisoft.booking.auth.AuthDto.RefreshTokenResponse;
import com.marisoft.booking.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @GetMapping("/me")
    public CurrentUserResponse getCurrentUser(@AuthenticationPrincipal User user) {
        return authService.getCurrentUser(user);
    }
}