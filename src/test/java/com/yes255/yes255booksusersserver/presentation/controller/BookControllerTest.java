package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.*;
import com.yes255.yes255booksusersserver.common.exception.QuantityInsufficientException;
import com.yes255.yes255booksusersserver.persistance.domain.enumtype.OperationType;
import com.yes255.yes255booksusersserver.presentation.dto.request.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.AuthorResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    @Mock
    private AuthorService authorService;

    @Mock
    private BookAuthorService bookAuthorService;


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DisplayName("모든 책 조회 - 성공")
    @Test
    void findAll_success() throws ParseException {
        // given
        List<BookResponse> mockBooks = List.of(
                new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description", "author1","Index",  "Updated Publisher",
                        sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120, 0, 0, 0),
                new BookResponse(2L, "ISBN11111111", "Name", "Description", "author1" ,"Index2", "Publisher",
                        sdf.parse("2020-06-14"), new BigDecimal("30.00"), new BigDecimal("24.99"), "updated.jpg", 120, 0, 0, 0)
        );

        Page<BookResponse> mockPage = new PageImpl<>(mockBooks, PageRequest.of(0, 10), mockBooks.size());

        when(bookService.getAllBooks(any(Pageable.class))).thenReturn(mockPage);

        // when
        Pageable pageable = PageRequest.of(0, 10);
        ResponseEntity<Page<BookResponse>> responseEntity = bookController.findAll(pageable);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockBooks.size(), responseEntity.getBody().getContent().size());
        assertEquals(mockBooks, responseEntity.getBody().getContent());
    }

    @DisplayName("특정 책 조회 - 성공")
    @Test
    void findById_success() throws ParseException {
        // given
        Long bookId = 1L;
        BookResponse mockBook = new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description", "author1", "Index", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120,0,0,0);
        when(bookService.getBook(bookId)).thenReturn(mockBook);

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
        CreateBookRequest request = new CreateBookRequest("1234567890", "Test Book", "Description", "index", "bookAuthor1, bookAuthor2", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), 100,"image.jpg");
        List<Long> categoryIdList = List.of(1L, 2L);

        BookResponse mockResponse = new BookResponse(1L, "1234567890", "Test Book", "Description", "bookAuthor1, bookAuthor2", "index", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "image.jpg", 100, 0, 0, 0);

        when(bookService.createBook(any(CreateBookRequest.class))).thenReturn(mockResponse);

        // Mock authorService responses
        when(authorService.getAuthorByName("bookAuthor1")).thenReturn(null);
        when(authorService.createAuthor(any(CreateAuthorRequest.class))).thenReturn(new AuthorResponse(1L, "bookAuthor1"));
        when(authorService.getAuthorByName("bookAuthor2")).thenReturn(null);
        when(authorService.createAuthor(any(CreateAuthorRequest.class))).thenReturn(new AuthorResponse(2L, "bookAuthor2"));

        // when
        ResponseEntity<BookResponse> responseEntity = bookController.create(request, categoryIdList, null, mock(BindingResult.class));

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockResponse, responseEntity.getBody());

        verify(bookCategoryService).createBookCategory(1L, 1L);
        verify(bookCategoryService).createBookCategory(1L, 2L);

        verifyNoInteractions(bookTagService);
    }

    @DisplayName("책 업데이트 - 성공")
    @Test
    void update_success() throws ParseException {
        // given
        UpdateBookRequest request = new UpdateBookRequest(1L, "Updated ISBN", "Updated Name", "Updated Description", "index", "author1", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), 120,"updated.jpg");
        List<Long> categoryIdList = List.of(1L, 2L);

        BookResponse mockResponse = new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description","author1" , "index", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120, 0, 0, 0);

        when(bookService.updateBook(any(UpdateBookRequest.class))).thenReturn(mockResponse);
        when(bookCategoryService.getBookCategoryByBookId(anyLong())).thenReturn(List.of());
        when(bookTagService.getBookTagByBookId(anyLong())).thenReturn(List.of());
        when(authorService.getAuthorByName("author1")).thenReturn(null);
        when(authorService.createAuthor(any())).thenReturn(new AuthorResponse(1L, "author1"));

        // when
        ResponseEntity<BookResponse> responseEntity = bookController.update(request, categoryIdList, null, mock(BindingResult.class));

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockResponse, responseEntity.getBody());

        // Verify interactions
        verify(bookService, times(1)).updateBook(any(UpdateBookRequest.class));
        verify(bookCategoryService, times(2)).createBookCategory(anyLong(), anyLong());
        verify(authorService, times(1)).createAuthor(any());
    }

    @DisplayName("책 재고 업데이트 - 성공")
    @Test
    void updateQuantity_success() throws ParseException {
        // given
        Long bookId = 1L;
        Integer quantity = 5;
        BookResponse mockBook = new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description", "bookAuthor1, bookAuthor2",  "Index","Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 150,0,0,0);
        when(bookService.getBook(bookId)).thenReturn(mockBook);

        List<Long> bookIdList = List.of(1L);
        List<Integer> quantityList = List.of(30);

        BookResponse mockUpdatedBook = new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description","bookAuthor1, bookAuthor2",  "Index", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120,0,0,0);
        when(bookService.updateBook(any(UpdateBookRequest.class))).thenReturn(mockUpdatedBook);

        // when
        ResponseEntity<List<BookResponse>> responseEntity = bookController.updateQuantity(new UpdateBookQuantityRequest(bookIdList, quantityList, OperationType.DECREASE));

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockUpdatedBook, responseEntity.getBody().get(0));
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
        verify(bookService, times(1)).removeBook(bookId);
    }

    @DisplayName("책 재고 업데이트 - 실패 (요청 수량이 재고보다 많음)")
    @Test
    void updateQuantity_failure_quantityInsufficient() throws ParseException {
        // given
        List<Long> bookIdList = List.of(1L);
        List<Integer> quantityList = List.of(15);
        BookResponse mockBook = new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description", "bookAuthor1, bookAuthor2", "Index","Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 10,0,0,0);
        when(bookService.getBook(1L)).thenReturn(mockBook);

        // then
        assertThrows(QuantityInsufficientException.class, () -> bookController.updateQuantity(new UpdateBookQuantityRequest(bookIdList, quantityList, OperationType.DECREASE)));

    }
}
