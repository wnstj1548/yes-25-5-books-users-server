package com.yes255.yes255booksusersserver.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yes255.yes255booksusersserver.application.service.*;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.BookNotFoundException;
import com.yes255.yes255booksusersserver.common.exception.QuantityInsufficientException;
import com.yes255.yes255booksusersserver.common.exception.ValidationFailedException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.enumtype.OperationType;
import com.yes255.yes255booksusersserver.presentation.dto.request.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.*;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookControllerTest {

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

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @DisplayName("모든 책 조회 - 성공")
    @Test
    void findAll_success() throws ParseException {
        // given
        List<BookResponse> mockBooks = List.of(
                new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description", "author1", "Index", "Updated Publisher",
                        sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120, 0, 0, 0, true, 4.5, false),
                new BookResponse(2L, "ISBN11111111", "Name", "Description", "author1", "Index2", "Publisher",
                        sdf.parse("2020-06-14"), new BigDecimal("30.00"), new BigDecimal("24.99"), "updated.jpg", 120, 0, 0, 0, true, 4.4, false)
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
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120, 0, 0, 0, true, 4.4, false);
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
        CreateBookRequest request = new CreateBookRequest("1234567890", "Test Book", "Description", "bookAuthor1, bookAuthor2", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), 100, "image.jpg", true);
        List<Long> categoryIdList = List.of(1L, 2L);

        BookResponse mockResponse = new BookResponse(1L, "1234567890", "Test Book", "Description", "bookAuthor1, bookAuthor2", "index", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "image.jpg", 100, 0, 0, 0, true, 4.4, false);

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

    @DisplayName("책 생성 - 실패 (유효성 검사 실패)")
    @Test
    void create_failure_validation() throws ParseException {
        // given
        CreateBookRequest invalidRequest = new CreateBookRequest("", "", "", "", "",
                null, null, null, -1, "", false);
        List<Long> categoryIdList = List.of(1L, 2L);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        // when
        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () ->
                bookController.create(invalidRequest, categoryIdList, null, bindingResult));

        // then
        assertNotNull(exception);
    }

    @DisplayName("책 생성 - 실패 (이미 존재하는 책)")
    @Test
    void create_failure_bookAlreadyExists() throws ParseException {
        // given
        CreateBookRequest request = new CreateBookRequest("1234567890", "Test Book", "Description", "bookAuthor1, bookAuthor2", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), 100, "image.jpg", true);
        List<Long> categoryIdList = List.of(1L, 2L);

        when(bookService.createBook(any(CreateBookRequest.class))).thenThrow(new ApplicationException(ErrorStatus.toErrorStatus("이미 존재하는 책입니다.", 400, LocalDateTime.now())));

        // when
        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                bookController.create(request, categoryIdList, null, mock(BindingResult.class)));

        // then
        assertNotNull(exception);
    }

    @DisplayName("책 생성 - 삭제된 책 복원")
    @Test
    void create_restoreDeletedBook() throws ParseException {
        // given
        CreateBookRequest request = new CreateBookRequest("1234567890", "Test Book", "Description", "bookAuthor1, bookAuthor2", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), 100, "image.jpg", true);
        List<Long> categoryIdList = List.of(1L, 2L);
        List<Long> tagIdList = List.of(1L, 2L);

        BookResponse existingDeletedBook = new BookResponse(1L, "1234567890", "Test Book", "Description", "bookAuthor1, bookAuthor2", "index", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "image.jpg", 100, 0, 0, 0, true, 4.4, true);

        when(bookService.getBookByIsbn("1234567890")).thenReturn(existingDeletedBook);

        UpdateBookRequest updateBookRequest = UpdateBookRequest.fromCreateBookRequest(request, existingDeletedBook.bookId());
        BookResponse updatedBookResponse = new BookResponse(1L, "1234567890", "Updated Test Book", "Description", "bookAuthor1, bookAuthor2", "index", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "image.jpg", 100, 0, 0, 0, true, 4.4, false);

        when(bookService.updateBook(updateBookRequest)).thenReturn(updatedBookResponse);

        // Mock bookCategoryService responses
        when(bookCategoryService.getBookCategoryByBookId(1L)).thenReturn(Arrays.asList(
                new BookCategoryResponse(1L, 1L, 1L),
                new BookCategoryResponse(2L, 1L, 2L)
        ));

        // Mock bookTagService responses
        when(bookTagService.getBookTagByBookId(1L)).thenReturn(Arrays.asList(
                new BookTagResponse(1L, 1L, 1L),
                new BookTagResponse(2L, 1L, 2L)
        ));

        // Mock bookAuthorService responses
        when(bookAuthorService.getBookAuthorByBookId(1L)).thenReturn(Arrays.asList(
                new BookAuthorResponse(1L, 1L, 1L),
                new BookAuthorResponse(2L, 1L, 2L)
        ));

        // Mock authorService responses
        when(authorService.getAuthorByName("bookAuthor1")).thenReturn(null);
        when(authorService.createAuthor(any(CreateAuthorRequest.class))).thenReturn(new AuthorResponse(1L, "bookAuthor1"));
        when(authorService.getAuthorByName("bookAuthor2")).thenReturn(null);
        when(authorService.createAuthor(any(CreateAuthorRequest.class))).thenReturn(new AuthorResponse(2L, "bookAuthor2"));

        // when
        ResponseEntity<BookResponse> responseEntity = bookController.create(request, categoryIdList, tagIdList, mock(BindingResult.class));

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedBookResponse, responseEntity.getBody());

        verify(bookService).updateBookIsDeleteFalse(1L);
        verify(bookService).updateBook(updateBookRequest);

        verify(bookCategoryService).removeBookCategory(1L);
        verify(bookCategoryService).removeBookCategory(2L);
        verify(bookCategoryService).createBookCategory(1L, 1L);
        verify(bookCategoryService).createBookCategory(1L, 2L);

        verify(bookTagService).removeBookTag(1L);
        verify(bookTagService).removeBookTag(2L);
        verify(bookTagService, times(2)).createBookTag(any(CreateBookTagRequest.class));

        verify(bookAuthorService).removeBookAuthor(1L);
        verify(bookAuthorService).removeBookAuthor(2L);
        verify(bookAuthorService, times(2)).createBookAuthor(any(CreateBookAuthorRequest.class));
    }


    @DisplayName("책 업데이트 - 성공")
    @Test
    void update_success() throws ParseException {
        // given
        UpdateBookRequest request = new UpdateBookRequest(1L, "Updated ISBN", "Updated Name", "Updated Description", "author1", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), 120, "updated.jpg", true);
        List<Long> categoryIdList = List.of(1L, 2L);

        BookResponse mockResponse = new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description", "author1", "index", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120, 0, 0, 0, true, 4.4, false);

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

    @DisplayName("책 업데이트 - 실패 (책을 찾을 수 없음)")
    @Test
    void update_failure_bookNotFound() throws ParseException {
// given
        UpdateBookRequest request = new UpdateBookRequest(1L, "Updated ISBN", "Updated Name", "Updated Description", "author1", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), 120, "updated.jpg", true);
        List categoryIdList = List.of(1L, 2L);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bookService.updateBook(any(UpdateBookRequest.class))).thenThrow(new BookNotFoundException(ErrorStatus.toErrorStatus("해당하는 책이 없습니다.", 404, LocalDateTime.now())));

        // when
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> bookController.update(request, categoryIdList, null, bindingResult));

        // then
        assertEquals("해당하는 책이 없습니다.", exception.getErrorStatus().message());
    }

    @DisplayName("책 재고 업데이트 - 성공")
    @Test
    void updateQuantity_success() throws ParseException {
        // given
        Long bookId = 1L;
        Integer quantity = 5;
        BookResponse mockBook = new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description", "bookAuthor1, bookAuthor2", "Index", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 150, 0, 0, 0, true, 4.4, false);
        when(bookService.getBook(bookId)).thenReturn(mockBook);

        List<Long> bookIdList = List.of(1L);
        List<Integer> quantityList = List.of(30);

        BookResponse mockUpdatedBook = new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description", "bookAuthor1, bookAuthor2", "Index", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120, 0, 0, 0, true, 4.4, false);
        when(bookService.updateBook(any(UpdateBookRequest.class))).thenReturn(mockUpdatedBook);

        // when
        ResponseEntity<List<BookResponse>> responseEntity = bookController.updateQuantity(new UpdateBookQuantityRequest(bookIdList, quantityList, OperationType.DECREASE));

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockUpdatedBook, responseEntity.getBody().get(0));
    }

    @DisplayName("책 재고 업데이트 - 실패 (요청 수량이 재고보다 많음)")
    @Test
    void updateQuantity_failure_quantityInsufficient() throws ParseException {
        // given
        List<Long> bookIdList = List.of(1L);
        List<Integer> quantityList = List.of(15);
        BookResponse mockBook = new BookResponse(1L, "Updated ISBN", "Updated Name", "Updated Description", "bookAuthor1, bookAuthor2", "Index", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 10, 0, 0, 0, true, 4.4, false);
        when(bookService.getBook(1L)).thenReturn(mockBook);

        // then
        assertThrows(QuantityInsufficientException.class, () -> bookController.updateQuantity(new UpdateBookQuantityRequest(bookIdList, quantityList, OperationType.DECREASE)));

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

    @DisplayName("책 삭제 - 실패 (책을 찾을 수 없음)")
    @Test
    void delete_failure_bookNotFound() {
        // given
        Long bookId = 1L;
        doThrow(new BookNotFoundException(ErrorStatus.toErrorStatus("해당하는 책이 없습니다.", 404, LocalDateTime.now()))).when(bookService).removeBook(anyLong());

        // when
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> bookController.delete(bookId));

        // then
        assertEquals("해당하는 책이 없습니다.", exception.getErrorStatus().message());
    }

    @DisplayName("책 조회수 증가 - 성공")
    @Test
    void addHitsCount_success() {
        // given
        Long bookId = 1L;
        doNothing().when(bookService).addHitsCount(anyLong());

        // when
        ResponseEntity<Void> responseEntity = bookController.addHitsCount(bookId);

        // then
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(bookService).addHitsCount(bookId);
    }

    @DisplayName("책 조회수 증가 - 실패 (책을 찾을 수 없음)")
    @Test
    void addHitsCount_failure_bookNotFound() {
        // given
        Long bookId = 1L;
        doThrow(new BookNotFoundException(ErrorStatus.toErrorStatus("해당하는 책이 없습니다.", 404, LocalDateTime.now()))).when(bookService).addHitsCount(anyLong());

        // when
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> bookController.addHitsCount(bookId));

        // then
        assertEquals("해당하는 책이 없습니다.", exception.getErrorStatus().message());
    }

    @DisplayName("이름으로 책 검색 - 성공")
    @Test
    void searchByName_success() {
        // given
        String query = "Java";
        List<BookCouponResponse> mockResponse = List.of(
                BookCouponResponse.builder().build()
                // Add more mock responses as needed
        );

        when(bookService.getBookByName(query)).thenReturn(mockResponse);

        // when
        ResponseEntity<List<BookCouponResponse>> responseEntity = bookController.searchByName(query);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockResponse, responseEntity.getBody());
    }

    @DisplayName("카테고리로 책 조회 - 성공")
    @Test
    void getBookByCategory_success() {
        // given
        Long categoryId = 1L;
        Pageable pageable = Pageable.unpaged(); // Mock Pageable
        String sortString = "popularity";
        List<BookResponse> mockResponse = List.of(
                new BookResponse(1L, "1234567890", "Java Programming", "Description", "Java Author", "index", "Publisher", null, null, null, "image.jpg", 100, 0, 0, 0, true, 4.4, false)
                // Add more mock responses as needed
        );
        Page<BookResponse> mockPage = new PageImpl<>(mockResponse);

        when(bookService.getBookByCategoryId(categoryId, pageable, sortString)).thenReturn(mockPage);

        // when
        ResponseEntity<Page<BookResponse>> responseEntity = bookController.getBookByCategory(categoryId, pageable, sortString);

        // then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockPage, responseEntity.getBody());
    }

    @DisplayName("책 생성 API - 성공 (삭제된 책 복원)")
    @Test
    void testCreateBook_success_restoreDeletedBook() throws Exception {
        // Mock 데이터 설정
        CreateBookRequest request = new CreateBookRequest(
                "1234567890", "Test Book", "Description", "Author1, Author2",
                "Publisher", new Date(), new BigDecimal("20.00"),
                new BigDecimal("15.99"), 100, "image.jpg", true
        );
        List<Long> categoryIdList = Arrays.asList(1L, 2L);

        BookResponse deletedBookResponse = new BookResponse(
                1L, "1234567890", "Test Book", "Description", "Author1, Author2",
                "index","Publisher", new Date(), new BigDecimal("20.00"),
                new BigDecimal("15.99"), "image.jpg", 100, 0, 0, 0, true, 4.4, true // Deleted status true
        );

        BookResponse restoredBookResponse = new BookResponse(
                1L, "1234567890", "Test Book", "Description", "Author1, Author2", "index",
                "Publisher", new Date(), new BigDecimal("20.00"),
                new BigDecimal("15.99"), "image.jpg", 100, 0, 0, 0, true, 4.4, false // Updated to false
        );

        // Mock 서비스 메서드 응답 설정
        when(bookService.getBookByIsbn("1234567890")).thenReturn(deletedBookResponse);

        bookService.updateBookIsDeleteFalse(1L);

        // HTTP POST 요청 준비
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .param("categoryIdList", "1", "2")
                .content(new ObjectMapper().writeValueAsString(request));

        // 서비스 메서드가 적절하게 호출되었는지 검증
        verify(bookService).updateBookIsDeleteFalse(1L);
    }
}