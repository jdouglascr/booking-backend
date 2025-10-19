package com.marisoft.booking.category;

import com.marisoft.booking.category.CategoryDto.CreateRequest;
import com.marisoft.booking.category.CategoryDto.UpdateRequest;
import com.marisoft.booking.shared.exception.BadRequestException;
import com.marisoft.booking.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category findById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoría no encontrada"));
    }

    @Transactional
    public void create(CreateRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new BadRequestException("Ya existe una categoría con ese nombre");
        }

        Category category = Category.builder()
                .name(request.name())
                .build();

        categoryRepository.save(category);
    }

    @Transactional
    public void update(Integer id, UpdateRequest request) {
        Category category = findById(id);

        if (categoryRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new BadRequestException("Ya existe una categoría con ese nombre");
        }

        category.setName(request.name());
        categoryRepository.save(category);
    }

    @Transactional
    public void delete(Integer id) {
        Category category = findById(id);
        categoryRepository.delete(category);
    }
}
