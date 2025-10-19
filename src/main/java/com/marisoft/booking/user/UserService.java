package com.marisoft.booking.user;

import com.marisoft.booking.shared.Role;
import com.marisoft.booking.shared.exception.BadRequestException;
import com.marisoft.booking.shared.exception.NotFoundException;
import com.marisoft.booking.user.UserDto.CreateRequest;
import com.marisoft.booking.user.UserDto.UpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    @Transactional
    public void create(CreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("El email ya está registrado");
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role() != null ? request.role() : Role.ROLE_STAFF)
                .isActive(true)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public void update(Integer id, UpdateRequest request) {
        User user = findById(id);
        
        if (!user.getEmail().equals(request.email())) {
            if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
                throw new BadRequestException("El email ya está registrado");
            }
            user.setEmail(request.email());
        }

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());

        if (request.role() != null) {
            user.setRole(request.role());
        }

        userRepository.save(user);
    }

    @Transactional
    public void delete(Integer id) {
        User user = findById(id);
        userRepository.delete(user);
    }
}