package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.CategoryServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.CategoryNotFoundException;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.BookCategory;
import com.yes255.yes255booksusersserver.persistance.domain.Category;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookCategoryRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private JpaCategoryRepository jpaCategoryRepository;

    @Mock
    private JpaBookCategoryRepository jpaBookCategoryRepository;

    @Mock
    private JpaBookRepository jpaBookRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;
    private Category parentCategory;
    private Category childCategory1;
    private Category childCategory2;
    private Book book;
    private BookCategory bookCategory2;
    private BookCategory bookCategory1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        parentCategory = new Category(1L, "Parent Category", null, null);
        testCategory = new Category(2L, "Test Category", parentCategory, null);
        parentCategory = new Category(1L, "Parent Category", null, null);
        childCategory1 = new Category(2L, "Child Category 1", parentCategory, null);
        childCategory2 = new Category(3L, "Child Category 2", parentCategory, null);
        book = Book.builder()
                .bookId(1L)
                .bookName("Sample Book")
                .build();
        bookCategory1 = new BookCategory(1L, book, new Category(1L, "Category 1", null, null));
        bookCategory2 = new BookCategory(2L, book, new Category(2L, "Category 2", null, null));
    }

    @DisplayName("카테고리 생성 - 성공")
    @Test
    void createCategory_success() {
        // given
        CreateCategoryRequest request = new CreateCategoryRequest("Test Category", parentCategory.getCategoryId());

        when(jpaCategoryRepository.save(any(Category.class))).thenReturn(testCategory);
        when(jpaCategoryRepository.findById(parentCategory.getCategoryId())).thenReturn(Optional.of(parentCategory));

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
        ApplicationException exception = assertThrows(ApplicationException.class, () -> categoryService.createCategory(null));
        assertEquals(null, exception.getMessage());
        verify(jpaCategoryRepository, never()).save(any(Category.class));
    }

    @DisplayName("카테고리 조회 - 성공")
    @Test
    void getCategory_success() {
        // given
        when(jpaCategoryRepository.findById(2L)).thenReturn(Optional.of(testCategory));

        // when
        CategoryResponse response = categoryService.getCategory(2L);

        // then
        assertNotNull(response);
        assertEquals(testCategory.getCategoryId(), response.categoryId());
        assertEquals(testCategory.getCategoryName(), response.categoryName());
    }

    @DisplayName("카테고리 조회 - 실패 (존재하지 않는 카테고리)")
    @Test
    void getCategory_failure_categoryNotFound() {
        // given
        when(jpaCategoryRepository.findById(2L)).thenReturn(Optional.empty());

        // then
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategory(2L));
        assertEquals(null, exception.getMessage());
    }

    @DisplayName("모든 카테고리 조회 (페이징) - 성공")
    @Test
    void getAllCategories_paged_success() {
        // given
        List<Category> categories = List.of(testCategory);
        Page<Category> categoryPage = new PageImpl<>(categories, PageRequest.of(0, 10), categories.size());

        when(jpaCategoryRepository.findAll(any(Pageable.class))).thenReturn(categoryPage);

        // when
        Pageable pageable = PageRequest.of(0, 10);
        Page<CategoryResponse> responsePage = categoryService.getAllCategories(pageable);

        // then
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals(testCategory.getCategoryId(), responsePage.getContent().get(0).categoryId());
    }

    @DisplayName("모든 카테고리 조회 - 성공")
    @Test
    void getAllCategories_success() {
        // given
        List<Category> categories = List.of(testCategory);
        when(jpaCategoryRepository.findAll()).thenReturn(categories);

        // when
        List<CategoryResponse> responses = categoryService.getAllCategories();

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testCategory.getCategoryId(), responses.get(0).categoryId());
    }

    @DisplayName("카테고리 업데이트 - 성공")
    @Test
    void updateCategory_success() {
        // given
        UpdateCategoryRequest request = new UpdateCategoryRequest(testCategory.getCategoryId(), "Updated Category", parentCategory.getCategoryId());
        Category updatedCategory = new Category(testCategory.getCategoryId(), "Updated Category", parentCategory, null);

        when(jpaCategoryRepository.existsById(testCategory.getCategoryId())).thenReturn(true);
        when(jpaCategoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(jpaCategoryRepository.findById(parentCategory.getCategoryId())).thenReturn(Optional.of(parentCategory));

        // when
        CategoryResponse response = categoryService.updateCategory(request);

        // then
        assertNotNull(response);
        assertEquals(updatedCategory.getCategoryId(), response.categoryId());
        assertEquals("Updated Category", response.categoryName());
    }

    @DisplayName("카테고리 수정 - 실패 (요청 값이 비어있는 경우)")
    @Test
    void updateCategory_failure_requestEmpty() {
        // then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> categoryService.updateCategory(null));
        assertEquals(null, exception.getMessage());
        verify(jpaCategoryRepository, never()).save(any(Category.class));
    }

    @DisplayName("카테고리 수정 - 실패 (존재하지 않는 카테고리)")
    @Test
    void updateCategory_failure_categoryNotFound() {
        // given
        UpdateCategoryRequest request = new UpdateCategoryRequest(2L, "Updated Category", null);

        when(jpaCategoryRepository.existsById(2L)).thenReturn(false);

        // then
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(request));
        assertEquals(null, exception.getMessage());
        verify(jpaCategoryRepository, never()).save(any(Category.class));
    }

    @DisplayName("카테고리 삭제 - 성공")
    @Test
    void removeCategory_success() {
        // given
        List<BookCategory> bookCategoryList = new ArrayList<>();

        when(jpaCategoryRepository.findById(testCategory.getCategoryId())).thenReturn(Optional.of(testCategory));
        when(jpaBookCategoryRepository.findByCategory(testCategory)).thenReturn(bookCategoryList);

        // when
        categoryService.removeCategory(testCategory.getCategoryId());

        // then
        verify(jpaBookCategoryRepository, times(1)).deleteAll(bookCategoryList);
        verify(jpaCategoryRepository, times(1)).deleteById(testCategory.getCategoryId());
    }

    @DisplayName("카테고리 삭제 - 실패 (존재하지 않는 카테고리)")
    @Test
    void removeCategory_failure_categoryNotFound() {
        // given
        when(jpaCategoryRepository.findById(testCategory.getCategoryId())).thenReturn(Optional.empty());

        // then
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> categoryService.removeCategory(testCategory.getCategoryId()));
        assertNull(exception.getMessage());
        verify(jpaCategoryRepository, never()).deleteById(anyLong());
    }

    @DisplayName("루트 카테고리 조회 - 성공")
    @Test
    void getRootCategories_success() {
        // given
        List<Category> categories = List.of(parentCategory, testCategory);
        when(jpaCategoryRepository.findAll()).thenReturn(categories);

        // when
        List<CategoryResponse> responses = categoryService.getRootCategories();

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(parentCategory.getCategoryId(), responses.get(0).categoryId());
    }

    @DisplayName("카테고리 조회 (부모 카테고리 ID로)")
    @Test
    void getCategoryByParentCategoryId_success() {
        // given
        List<Category> allCategories = List.of(parentCategory, childCategory1, childCategory2);
        when(jpaCategoryRepository.findAll()).thenReturn(allCategories);

        // when
        List<CategoryResponse> responses = categoryService.getCategoryByParentCategoryId(parentCategory.getCategoryId());

        // then
        assertEquals(2, responses.size());
        assertEquals(childCategory1.getCategoryId(), responses.get(0).categoryId());
        assertEquals(childCategory1.getCategoryName(), responses.get(0).categoryName());
        assertEquals(childCategory2.getCategoryId(), responses.get(1).categoryId());
        assertEquals(childCategory2.getCategoryName(), responses.get(1).categoryName());
    }

    @DisplayName("책 ID로 카테고리 ID 조회 - 성공")
    @Test
    void getCategoryIdByBookId_success() {
        // given
        List<BookCategory> bookCategories = List.of(bookCategory1, bookCategory2);
        when(jpaBookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(jpaBookCategoryRepository.findByBook(book)).thenReturn(bookCategories);

        // when
        List<Long> categoryIdList = categoryService.getCategoryIdByBookId(book.getBookId());

        // then
        assertEquals(2, categoryIdList.size());
        assertEquals(bookCategory1.getCategory().getCategoryId(), categoryIdList.get(0));
        assertEquals(bookCategory2.getCategory().getCategoryId(), categoryIdList.get(1));
    }

    @DisplayName("책 ID로 카테고리 ID 조회 - 실패 (알맞은 책이 없는 경우)")
    @Test
    void getCategoryIdByBookId_failure_bookNotFound() {
        // given
        when(jpaBookRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThrows(ApplicationException.class, () -> categoryService.getCategoryIdByBookId(1L));
    }
}