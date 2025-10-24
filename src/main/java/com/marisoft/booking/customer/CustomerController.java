package com.marisoft.booking.customer;

import com.marisoft.booking.customer.CustomerDto.Response;
import com.marisoft.booking.customer.CustomerDto.CreateRequest;
import com.marisoft.booking.customer.CustomerDto.UpdateRequest;
import com.marisoft.booking.shared.dto.MessageResponse;
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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createCustomer(@Valid @RequestBody CreateRequest request) {
        customerService.create(request);
        return new MessageResponse("Cliente creado exitosamente");
    }

    @GetMapping
    public List<Response> getAllCustomers() {
        return customerService.findAll().stream()
                .map(Response::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public Response getCustomerById(@PathVariable Integer id) {
        return Response.fromEntity(customerService.findById(id));
    }

    @GetMapping("/email/{email}")
    public Response getCustomerByEmail(@PathVariable String email) {
        return Response.fromEntity(customerService.findByEmail(email));
    }

    @PutMapping("/{id}")
    public MessageResponse updateCustomer(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRequest request
    ) {
        customerService.update(id, request);
        return new MessageResponse("Cliente actualizado exitosamente");
    }

    @DeleteMapping("/{id}")
    public MessageResponse deleteCustomer(@PathVariable Integer id) {
        customerService.delete(id);
        return new MessageResponse("Cliente eliminado exitosamente");
    }
}