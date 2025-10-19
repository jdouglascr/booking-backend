package com.marisoft.booking.category;

import com.marisoft.booking.category.CategoryDto.CreateRequest;
import com.marisoft.booking.category.CategoryDto.Response;
import com.marisoft.booking.category.CategoryDto.UpdateRequest;
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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Response> getAllCategories() {
        return categoryService.findAll().stream()
                .map(Response::fromEntity)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Response getCategoryById(@PathVariable Integer id) {
        return Response.fromEntity(categoryService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse createCategory(@Valid @RequestBody CreateRequest request) {
        categoryService.create(request);
        return new MessageResponse("Categoría creada exitosamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public MessageResponse updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRequest request
    ) {
        categoryService.update(id, request);
        return new MessageResponse("Categoría actualizada exitosamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public MessageResponse deleteCategory(@PathVariable Integer id) {
        categoryService.delete(id);
        return new MessageResponse("Categoría eliminada exitosamente");
    }
}
