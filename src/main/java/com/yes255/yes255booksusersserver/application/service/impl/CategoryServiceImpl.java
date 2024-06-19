package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.CategoryService;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Category;
import com.yes255.yes255booksusersserver.common.exception.CategoryNotFoundException;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCategoryRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Transactional
    @Override
    public CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest) {

        if(Objects.isNull(createCategoryRequest)) {
            throw new ApplicationException(ErrorStatus.toErrorStatus("요청 값이 비어있습니다.", 400, LocalDateTime.now()));
        }

        return toResponse(jpaCategoryRepository.save(createCategoryRequest.toEntity()));
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryResponse findCategory(long categoryId) {

        Category category = jpaCategoryRepository.findById(categoryId).orElse(null);
        if(category == null) {
            throw new CategoryNotFoundException(ErrorStatus.toErrorStatus("알맞은 카테고리를 찾을 수 없습니다.", 404, LocalDateTime.now()));
        }

        return toResponse(category);

    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponse> findAllCategories() {
        return jpaCategoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    @Override
    public CategoryResponse updateCategory(UpdateCategoryRequest updateCategoryRequest) {

        if(Objects.isNull(updateCategoryRequest)) {
            throw new ApplicationException(ErrorStatus.toErrorStatus("요청 값이 비어있습니다.", 400, LocalDateTime.now()));
        }

        if(jpaCategoryRepository.existsById(updateCategoryRequest.categoryId())) {
            throw new CategoryNotFoundException(ErrorStatus.toErrorStatus("카테고리를 찾을 수 없습니다.", 400, LocalDateTime.now()));
        }

        return toResponse(jpaCategoryRepository.save(updateCategoryRequest.toEntity()));
    }

    @Transactional
    @Override
    public void deleteCategory(long categoryId) {

        if(!jpaCategoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(ErrorStatus.toErrorStatus("카테고리를 찾을 수 없습니다.", 400, LocalDateTime.now()));
        }

        jpaCategoryRepository.deleteById(categoryId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponse> findRootCategories() {

        List<CategoryResponse> rootCategories = new ArrayList<>();

        for(CategoryResponse category : findAllCategories()) {
            if(Objects.isNull(category.parentCategory())) {
                rootCategories.add(category);
            }
        }

        return rootCategories;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponse> findCategoryByParentCategoryId(long parentCategoryId) {
        return findAllCategories().stream()
                .filter(category -> Objects.nonNull(category.parentCategory()) && category.parentCategory().getCategoryId() == parentCategoryId)
                .collect(Collectors.toList());
    }
}
