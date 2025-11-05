package com.marisoft.booking.auth;

import com.marisoft.booking.auth.AuthDto.CurrentUserResponse;
import com.marisoft.booking.auth.AuthDto.UpdateProfileRequest;
import com.marisoft.booking.shared.dto.MessageResponse;
import com.marisoft.booking.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public CurrentUserResponse getCurrentUser(@AuthenticationPrincipal User user) {
        return authService.getCurrentUser(user);
    }

    @PutMapping("/me")
    public MessageResponse updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        authService.updateProfile(user, request);
        return new MessageResponse("Perfil actualizado exitosamente");
    }
}