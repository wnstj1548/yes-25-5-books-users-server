package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.BookCategoryServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.BookCategory;
import com.yes255.yes255booksusersserver.persistance.domain.Category;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookCategoryRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCategoryRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookCategoryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookCategoryServiceImplTest {

    @Mock
    private JpaBookCategoryRepository jpaBookCategoryRepository;

    @Mock
    private JpaBookRepository jpaBookRepository;

    @Mock
    private JpaCategoryRepository jpaCategoryRepository;

    @InjectMocks
    private BookCategoryServiceImpl bookCategoryService;

    private Book testBook;
    private Category testCategory;
    private BookCategory testBookCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        testBook = new Book(1L, "Test Book ISBN", "Test Author", "description", "index", "author", "publisher", null, new BigDecimal("12345"), new BigDecimal("10000"), "image", 10, 0,0,0);
        testCategory = new Category(1L, "Test Category", null, null);
        testBookCategory = new BookCategory(1L, testBook, testCategory);

        // Mocking repository methods
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(jpaBookCategoryRepository.findById(1L)).thenReturn(Optional.of(testBookCategory));
        when(jpaBookCategoryRepository.existsById(1L)).thenReturn(true);
        when(jpaBookCategoryRepository.save(any())).thenReturn(testBookCategory);
        when(jpaBookCategoryRepository.findAll()).thenReturn(Collections.singletonList(testBookCategory));
        when(jpaBookCategoryRepository.findByBook(testBook)).thenReturn(Collections.singletonList(testBookCategory));
        when(jpaBookCategoryRepository.findByCategory(testCategory)).thenReturn(Collections.singletonList(testBookCategory));
    }

    @DisplayName("북 카테고리 생성 - 성공")
    @Test
    void createBookCategory_success() {
        // when
        BookCategoryResponse response = bookCategoryService.createBookCategory(1L, 1L);

        // then
        assertNotNull(response);
        assertEquals(testBook.getBookId(), response.bookId());
        assertEquals(testCategory.getCategoryId(), response.categoryId());
    }

    @DisplayName("북 카테고리 조회 - 성공")
    @Test
    void findBookCategory_success() {
        // when
        BookCategoryResponse response = bookCategoryService.findBookCategory(1L);

        // then
        assertNotNull(response);
        assertEquals(testBookCategory.getBookCategoryId(), response.bookCategoryId());
        assertEquals(testBookCategory.getBook().getBookId(), response.bookId());
        assertEquals(testBookCategory.getCategory().getCategoryId(), response.categoryId());
    }

    @DisplayName("북 카테고리 조회 - 실패 (존재하지 않는 북 카테고리)")
    @Test
    void findBookCategory_failure_bookCategoryNotFound() {
        // given
        when(jpaBookCategoryRepository.findById(2L)).thenReturn(Optional.empty());

        // then
        assertThrows(ApplicationException.class, () -> bookCategoryService.findBookCategory(2L));
    }

    @DisplayName("북 카테고리 조회 (책 ID로) - 성공")
    @Test
    void findBookCategoryByBookId_success() {
        // when
        List<BookCategoryResponse> responses = bookCategoryService.findBookCategoryByBookId(1L);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testBook.getBookId(), responses.get(0).bookId());
        assertEquals(testCategory.getCategoryId(), responses.get(0).categoryId());
    }

    @DisplayName("북 카테고리 조회 (카테고리 ID로) - 성공")
    @Test
    void findBookCategoryByCategoryId_success() {
        // when
        List<BookCategoryResponse> responses = bookCategoryService.findBookCategoryByCategoryId(1L);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testBook.getBookId(), responses.get(0).bookId());
        assertEquals(testCategory.getCategoryId(), responses.get(0).categoryId());
    }

    @DisplayName("모든 북 카테고리 조회 - 성공")
    @Test
    void findAllBookCategories_success() {
        // when
        List<BookCategoryResponse> responses = bookCategoryService.findAllBookCategories();

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testBookCategory.getBookCategoryId(), responses.get(0).bookCategoryId());
        assertEquals(testBookCategory.getBook().getBookId(), responses.get(0).bookId());
        assertEquals(testBookCategory.getCategory().getCategoryId(), responses.get(0).categoryId());
    }

    @DisplayName("북 카테고리 업데이트 - 성공")
    @Test
    void updateBookCategory_success() {
        // given
        UpdateBookCategoryRequest request = new UpdateBookCategoryRequest(1L, 1L, 1L);
        when(jpaBookCategoryRepository.existsById(1L)).thenReturn(true);

        // when
        BookCategoryResponse response = bookCategoryService.updateBookCategory(request);

        // then
        assertNotNull(response);
        assertEquals(testBookCategory.getBookCategoryId(), response.bookCategoryId());
        assertEquals(testBookCategory.getBook().getBookId(), response.bookId());
        assertEquals(testBookCategory.getCategory().getCategoryId(), response.categoryId());
    }

    @DisplayName("북 카테고리 업데이트 - 실패 (존재하지 않는 북 카테고리)")
    @Test
    void updateBookCategory_failure_bookCategoryNotFound() {
        // given
        UpdateBookCategoryRequest request = new UpdateBookCategoryRequest(2L, 1L, 1L);
        when(jpaBookCategoryRepository.existsById(2L)).thenReturn(false);

        // then
        assertThrows(ApplicationException.class, () -> bookCategoryService.updateBookCategory(request));
    }

    @DisplayName("북 카테고리 삭제 - 성공")
    @Test
    void deleteBookCategory_success() {
        // when
        bookCategoryService.deleteBookCategory(1L);

        // then
        verify(jpaBookCategoryRepository, times(1)).deleteById(1L);
    }

    @DisplayName("북 카테고리 삭제 - 실패 (존재하지 않는 북 카테고리)")
    @Test
    void deleteBookCategory_failure_bookCategoryNotFound() {
        // given
        doThrow(new ApplicationException(ErrorStatus.toErrorStatus("삭제할 북 카테고리를 찾을 수 없습니다.", 400, LocalDateTime.now())))
                .when(jpaBookCategoryRepository).deleteById(2L);

        // then
        assertThrows(ApplicationException.class, () -> bookCategoryService.deleteBookCategory(2L));
    }
}
