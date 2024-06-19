package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.BookCategoryService;
import com.yes255.yes255booksusersserver.application.service.BookService;
import com.yes255.yes255booksusersserver.application.service.BookTagService;
import com.yes255.yes255booksusersserver.common.exception.QuantityInsufficientException;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class BookControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private BookCategoryService bookCategoryService;

    @Mock
    private BookTagService bookTagService;

    @InjectMocks
    private BookController bookController;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DisplayName("모든 책 조회 - 성공")
    @Test
    void findAll_success() throws ParseException {

        BookResponse updatedBook = new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description", "Index","Updated Author", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120,0,0,0);
        // given
        List<BookResponse> mockBooks = List.of(
                new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description", "Index","Updated Author", "Updated Publisher",
                        sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120,0,0,0),
        new BookResponse(2L, "ISBN11111111", "Name", "Description", "Index2","Author", "Publisher",
                sdf.parse("2020-06-14"), new BigDecimal("30.00"), new BigDecimal("24.99"), "updated.jpg", 120,0,0,0)
        );
        when(bookService.findAllBooks()).thenReturn(mockBooks);

        // when
        ResponseEntity<List<BookResponse>> responseEntity = bookController.findAll();

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockBooks.size(), responseEntity.getBody().size());
        assertEquals(mockBooks, responseEntity.getBody());
    }

    @DisplayName("특정 책 조회 - 성공")
    @Test
    void findById_success() throws ParseException {
        // given
        Long bookId = 1L;
        BookResponse mockBook = new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description", "Index","Updated Author", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120,0,0,0);
        when(bookService.findBook(bookId)).thenReturn(mockBook);

        // when
        ResponseEntity<BookResponse> responseEntity = bookController.findById(bookId);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockBook, responseEntity.getBody());
    }

    @DisplayName("책 생성 - 성공")
    @Test
    void create_success() throws ParseException {
        // given
        CreateBookRequest request = new CreateBookRequest("1234567890", "Test Book", "Description", "index", "Author", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "image.jpg", 100);
        List<Long> categoryIdList = List.of(1L, 2L);

        BookResponse mockResponse = new BookResponse(1L, "1234567890", "Test Book", "Description", "index","Author", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "image.jpg", 100,0,0,0);

        when(bookService.createBook(any(CreateBookRequest.class))).thenReturn(mockResponse);

        // when
        ResponseEntity<BookResponse> responseEntity = bookController.create(request, categoryIdList, null, mock(BindingResult.class));

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockResponse, responseEntity.getBody());
    }

    @DisplayName("책 업데이트 - 성공")
    @Test
    void update_success() throws ParseException {
        // given
        UpdateBookRequest request = new UpdateBookRequest(1L, "Updated ISBN", "Updated Name", "Updated Description", "index" ,"Updated Author", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120,0,0,0);
        List<Long> categoryIdList = List.of(1L, 2L);

        BookResponse mockResponse = new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description", "Index","Updated Author", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120,0,0,0);

        when(bookService.updateBook(any(UpdateBookRequest.class))).thenReturn(mockResponse);
        when(bookCategoryService.findBookCategoryByBookId(anyLong())).thenReturn(List.of());
        when(bookTagService.findBookTagByBookId(anyLong())).thenReturn(List.of());

        // when
        ResponseEntity<BookResponse> responseEntity = bookController.update(request, categoryIdList, null, mock(BindingResult.class));

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockResponse, responseEntity.getBody());
    }

    @DisplayName("책 재고 업데이트 - 성공")
    @Test
    void updateQuantity_success() throws ParseException {
        // given
        Long bookId = 1L;
        Integer quantity = 5;
        BookResponse mockBook = new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description", "Index","Updated Author", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 150,0,0,0);
        when(bookService.findBook(bookId)).thenReturn(mockBook);

        BookResponse mockUpdatedBook = new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description", "Index","Updated Author", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120,0,0,0);
        when(bookService.updateBook(any(UpdateBookRequest.class))).thenReturn(mockUpdatedBook);

        // when
        ResponseEntity<BookResponse> responseEntity = bookController.updateQuantity(bookId, quantity);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockUpdatedBook, responseEntity.getBody());
    }

    @DisplayName("책 삭제 - 성공")
    @Test
    void delete_success() {
        // given
        Long bookId = 1L;

        // when
        ResponseEntity<Void> responseEntity = bookController.delete(bookId);

        // then
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(bookService, times(1)).deleteBook(bookId);
    }

    @DisplayName("책 재고 업데이트 - 실패 (요청 수량이 재고보다 많음)")
    @Test
    void updateQuantity_failure_quantityInsufficient() throws ParseException {
        // given
        Long bookId = 1L;
        Integer quantity = 15;
        BookResponse mockBook = new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description", "Index","Updated Author", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 10,0,0,0);
        when(bookService.findBook(bookId)).thenReturn(mockBook);

        // then
        assertThrows(QuantityInsufficientException.class, () -> bookController.updateQuantity(bookId, quantity));

    }
}
