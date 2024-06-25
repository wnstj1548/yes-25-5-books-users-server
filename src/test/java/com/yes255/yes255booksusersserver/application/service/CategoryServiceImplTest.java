package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.CategoryServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.CategoryNotFoundException;
import com.yes255.yes255booksusersserver.persistance.domain.BookCategory;
import com.yes255.yes255booksusersserver.persistance.domain.Category;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookCategoryRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCategoryRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CategoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private JpaCategoryRepository jpaCategoryRepository;

    @Mock
    private JpaBookCategoryRepository jpaBookCategoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        testCategory = Category.builder()
                .categoryId(1L)
                .categoryName("categoryName")
                .parentCategory(null)
                .build();
    }

    @DisplayName("카테고리 생성 - 성공")
    @Test
    void createCategory_success() {
        // given
        CreateCategoryRequest request = new CreateCategoryRequest("categoryName", null);

        when(jpaCategoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // when
        CategoryResponse response = categoryService.createCategory(request);

        // then
        assertNotNull(response);
        assertEquals(testCategory.getCategoryId(), response.categoryId());
        assertEquals(testCategory.getCategoryName(), response.categoryName());

        verify(jpaCategoryRepository, times(1)).save(any(Category.class));
    }

    @DisplayName("카테고리 생성 - 실패 (요청 값이 비어있는 경우)")
    @Test
    void createCategory_failure_requestEmpty() {
        // then
        assertThrows(ApplicationException.class, () -> categoryService.createCategory(null));

        verify(jpaCategoryRepository, never()).save(any(Category.class));
    }

    @DisplayName("카테고리 조회 - 성공")
    @Test
    void findCategory_success() {
        // given
        when(jpaCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // when
        CategoryResponse response = categoryService.findCategory(1L);

        // then
        assertNotNull(response);
        assertEquals(testCategory.getCategoryId(), response.categoryId());
        assertEquals(testCategory.getCategoryName(), response.categoryName());
    }

    @DisplayName("카테고리 조회 - 실패 (존재하지 않는 카테고리)")
    @Test
    void findCategory_failure_categoryNotFound() {
        // given
        when(jpaCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThrows(CategoryNotFoundException.class, () -> categoryService.findCategory(1L));
    }

    @DisplayName("카테고리 업데이트 - 성공")
    @Test
    void updateCategory_success() {
        // given
        UpdateCategoryRequest request = new UpdateCategoryRequest(1L, "UpdatedCategoryName", null);
        Category updatedCategory = new Category(1L, "UpdatedCategoryName", null, null);

        // Mock 설정
        when(jpaCategoryRepository.existsById(request.categoryId())).thenReturn(true);
        when(jpaCategoryRepository.save(any())).thenReturn(updatedCategory);

        // when
        CategoryResponse response = categoryService.updateCategory(request);

        // then
        verify(jpaCategoryRepository, times(1)).existsById(request.categoryId());
        verify(jpaCategoryRepository, times(1)).save(any());

        assertEquals(updatedCategory.getCategoryId(), response.categoryId());
        assertEquals(updatedCategory.getCategoryName(), response.categoryName());
        assertNull(response.parentCategoryId());
    }

    @DisplayName("카테고리 수정 - 실패 (요청 값이 비어있는 경우)")
    @Test
    void updateCategory_failure_requestEmpty() {
        // then
        assertThrows(ApplicationException.class, () -> categoryService.updateCategory(null));

        verify(jpaCategoryRepository, never()).save(any(Category.class));
    }

    @DisplayName("카테고리 수정 - 실패 (존재하지 않는 카테고리)")
    @Test
    void updateCategory_failure_categoryNotFound() {
        // given
        UpdateCategoryRequest request = new UpdateCategoryRequest(1L, "UpdatedCategory", null);

        when(jpaCategoryRepository.existsById(1L)).thenReturn(false);

        // then
        assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(request));

        verify(jpaCategoryRepository, never()).save(any(Category.class));
    }

    @DisplayName("카테고리 삭제 - 성공")
    @Test
    void deleteCategory_success() {
        // given
        Category category = new Category(1L, "Test Category", null, null);

        List<BookCategory> bookCategoryList = new ArrayList<>();

        // Mock 설정
        when(jpaCategoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(jpaBookCategoryRepository.findByCategory(category)).thenReturn(bookCategoryList);

        // when
        categoryService.removeCategory(1L);

        // then
        verify(jpaCategoryRepository, times(1)).findById(1L);
        verify(jpaBookCategoryRepository, times(1)).findByCategory(category);
        verify(jpaBookCategoryRepository, times(1)).deleteAll(bookCategoryList);
        verify(jpaCategoryRepository, times(1)).deleteById(1L);
    }

    @DisplayName("카테고리 삭제 - 실패 (존재하지 않는 카테고리)")
    @Test
    void deleteCategory_failure_categoryNotFound() {
        // given
        when(jpaCategoryRepository.existsById(1L)).thenReturn(false);

        // then
        assertThrows(CategoryNotFoundException.class, () -> categoryService.removeCategory(1L));

        verify(jpaCategoryRepository, never()).deleteById(1L);
    }

    @DisplayName("모든 카테고리 조회 - 성공")
    @Test
    void findAllCategories_success() {
        // given
        List<Category> categories = Arrays.asList(testCategory, new Category(2L, "TestCategory2", null, null));
        when(jpaCategoryRepository.findAll()).thenReturn(categories);

        // when
        List<CategoryResponse> responses = categoryService.getAllCategories();

        // then
        assertEquals(categories.size(), responses.size());
        assertEquals(categories.get(0).getCategoryId(), responses.get(0).categoryId());
        assertEquals(categories.get(0).getCategoryName(), responses.get(0).categoryName());
        assertEquals(categories.get(1).getCategoryId(), responses.get(1).categoryId());
        assertEquals(categories.get(1).getCategoryName(), responses.get(1).categoryName());
    }

    @DisplayName("부모 카테고리 ID로 카테고리 조회 - 성공")
    @Test
    void findCategoryByParentCategoryId_success() {
        // given
        Category parentCategory = new Category(1L, "ParentCategory", null, null);
        Category childCategory = new Category(2L, "ChildCategory", parentCategory, null);

        // 모의 객체 설정
        when(jpaCategoryRepository.findAll()).thenReturn(Arrays.asList(parentCategory, childCategory));

        // when
        List<CategoryResponse> responses = categoryService.getCategoryByParentCategoryId(1L);

        // then
        assertEquals(1, responses.size());
        assertEquals(childCategory.getCategoryId(), responses.get(0).categoryId());
        assertEquals(childCategory.getCategoryName(), responses.get(0).categoryName());
        assertEquals(parentCategory.getCategoryId(), responses.get(0).parentCategoryId());
    }
}
