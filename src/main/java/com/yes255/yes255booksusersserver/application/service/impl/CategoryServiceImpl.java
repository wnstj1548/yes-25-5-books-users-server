package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.CategoryService;
import com.yes255.yes255booksusersserver.persistance.domain.Category;
import com.yes255.yes255booksusersserver.persistance.exception.CategoryNotFoundException;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCategoryRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final JpaCategoryRepository jpaCategoryRepository;

    public CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .parentCategory(category.getParentCategory())
                .build();
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest) {

        if(Objects.isNull(createCategoryRequest)) {
            throw new IllegalArgumentException();
        }

        Category category = jpaCategoryRepository.save(createCategoryRequest.toEntity());

        return toResponse(category);
    }

    @Override
    public CategoryResponse findByCategoryId(long categoryId) {

        Category category = jpaCategoryRepository.findById(categoryId).orElse(null);
        if(category == null) {
            throw new CategoryNotFoundException();
        }

        return toResponse(category);

    }

    @Override
    public List<CategoryResponse> findAllCategories() {
        return jpaCategoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public CategoryResponse updateCategory(UpdateCategoryRequest updateCategoryRequest) {

        if(Objects.isNull(updateCategoryRequest)) {
            throw new IllegalArgumentException("category request cannot be null");
        }

        if(jpaCategoryRepository.existsById(updateCategoryRequest.categoryId())) {
            throw new IllegalArgumentException("category not found");
        }

        return toResponse(jpaCategoryRepository.save(updateCategoryRequest.toEntity()));
    }

    @Override
    public void deleteByCategoryId(long categoryId) {

        if(!jpaCategoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("category not found");
        }

        jpaCategoryRepository.deleteById(categoryId);
    }

    @Override
    public List<CategoryResponse> findFirstStepCategories() {

        List<CategoryResponse> firstStepCategories = new ArrayList<>();

        for(CategoryResponse category : findAllCategories()) {
            if(Objects.isNull(category.parentCategory())) {
                firstStepCategories.add(category);
            }
        }

        return firstStepCategories;
    }

    @Override
    public List<CategoryResponse> findByParentCategoryId(long parentCategoryId) {
        return findAllCategories().stream()
                .filter(category -> Objects.nonNull(category.parentCategory()) && category.parentCategory().getCategoryId() == parentCategoryId)
                .collect(Collectors.toList());
    }
}
