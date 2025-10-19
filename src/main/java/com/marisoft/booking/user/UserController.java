package com.marisoft.booking.user;

import com.marisoft.booking.shared.dto.MessageResponse;
import com.marisoft.booking.user.UserDto.CreateRequest;
import com.marisoft.booking.user.UserDto.Response;
import com.marisoft.booking.user.UserDto.UpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Response> getAllUsers() {
        return userService.findAll().stream()
                .map(Response::fromEntity)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Response getUserById(@PathVariable Integer id) {
        return Response.fromEntity(userService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createUser(@Valid @RequestBody CreateRequest request) {
        userService.create(request);
        return new MessageResponse("Usuario creado exitosamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public MessageResponse updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRequest request
    ) {
        userService.update(id, request);
        return new MessageResponse("Usuario actualizado exitosamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public MessageResponse deleteUser(@PathVariable Integer id) {
        userService.delete(id);
        return new MessageResponse("Usuario eliminado exitosamente");
    }
}
