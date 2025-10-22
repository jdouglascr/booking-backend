package com.marisoft.booking.customer;

import com.marisoft.booking.customer.CustomerDto.UpdateRequest;
import com.marisoft.booking.customer.CustomerDto.UpsertRequest;
import com.marisoft.booking.shared.exception.BadRequestException;
import com.marisoft.booking.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer findById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
    }

    @Transactional(readOnly = true)
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
    }

    @Transactional
    public Customer upsert(UpsertRequest request) {
        return customerRepository.findByEmail(request.email())
                .map(existing -> {
                    boolean updated = false;

                    if (!existing.getFirstName().equals(request.firstName())) {
                        existing.setFirstName(request.firstName());
                        updated = true;
                    }
                    if (!existing.getLastName().equals(request.lastName())) {
                        existing.setLastName(request.lastName());
                        updated = true;
                    }
                    if (!existing.getPhone().equals(request.phone())) {
                        existing.setPhone(request.phone());
                        updated = true;
                    }

                    return updated ? customerRepository.save(existing) : existing;
                })
                .orElseGet(() -> {
                    Customer newCustomer = Customer.builder()
                            .firstName(request.firstName())
                            .lastName(request.lastName())
                            .email(request.email())
                            .phone(request.phone())
                            .build();
                    return customerRepository.save(newCustomer);
                });
    }

    @Transactional
    public void update(Integer id, UpdateRequest request) {
        Customer customer = findById(id);

        if (!customer.getEmail().equals(request.email())) {
            if (customerRepository.existsByEmailAndIdNot(request.email(), id)) {
                throw new BadRequestException("El email ya está registrado");
            }
            customer.setEmail(request.email());
        }

        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setPhone(request.phone());

        customerRepository.save(customer);
    }

    @Transactional
    public void delete(Integer id) {
        Customer customer = findById(id);
        customerRepository.delete(customer);
    }
}