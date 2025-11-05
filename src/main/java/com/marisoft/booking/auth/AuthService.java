package com.marisoft.booking.auth;

import com.marisoft.booking.auth.AuthDto.CurrentUserResponse;
import com.marisoft.booking.auth.AuthDto.LoginRequest;
import com.marisoft.booking.auth.AuthDto.LoginResponse;
import com.marisoft.booking.auth.AuthDto.RefreshTokenRequest;
import com.marisoft.booking.auth.AuthDto.RefreshTokenResponse;
import com.marisoft.booking.auth.AuthDto.UpdateProfileRequest;
import com.marisoft.booking.shared.exception.BadRequestException;
import com.marisoft.booking.shared.security.JwtUtil;
import com.marisoft.booking.user.User;
import com.marisoft.booking.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadRequestException("Credenciales inválidas");
        }

        if (!user.getIsActive()) {
            throw new BadRequestException("Usuario inactivo");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken, "Inicio de sesión exitoso");
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BadRequestException("Refresh token inválido o expirado");
        }

        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new BadRequestException("Token inválido");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        if (!user.getIsActive()) {
            throw new BadRequestException("Usuario inactivo");
        }

        String newAccessToken = jwtUtil.generateAccessToken(user);

        return new RefreshTokenResponse(newAccessToken, refreshToken);
    }

    public CurrentUserResponse getCurrentUser(User user) {
        return new CurrentUserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRole().name(),
                user.getIsActive()
        );
    }

    @Transactional
    public void updateProfile(User user, UpdateProfileRequest request) {
        if (!user.getEmail().equals(request.email())) {
            if (userRepository.existsByEmailAndIdNot(request.email(), user.getId())) {
                throw new BadRequestException("El email ya está registrado");
            }
        }

        if (!request.phone().matches("\\+56[0-9]{9}")) {
            throw new BadRequestException("El teléfono debe tener el formato +56XXXXXXXXX");
        }

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhone(request.phone());

        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        userRepository.save(user);
    }
}