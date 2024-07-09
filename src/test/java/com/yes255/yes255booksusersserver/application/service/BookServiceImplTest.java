package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.BookServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.BookNotFoundException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.*;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookCouponResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookOrderResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookServiceImplTest {

    @Mock
    private JpaBookRepository jpaBookRepository;
    @Mock
    private JpaCategoryRepository jpaCategoryRepository;
    @Mock
    private JpaBookCategoryRepository jpaBookCategoryRepository;
    @Mock
    private JpaBookTagRepository jpaBookTagRepository;
    @Mock
    private JpaCartBookRepository jpaCartBookRepository;
    @Mock
    private JpaBookAuthorRepository jpaBookAuthorRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;
    private Category testCategory;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    void setUp() throws ParseException {
        MockitoAnnotations.initMocks(this);
        testBook = new Book(1L, "1234567890", "Test Book", "Description", "Index", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "image.jpg",
                100, 0, 0, 0, true, false);
        testCategory = new Category(1L, "Test Category", null, null);
    }

    @DisplayName("책 생성 - 성공")
    @Test
    void createBook_success() throws ParseException {
        // given
        CreateBookRequest request = new CreateBookRequest("1234567890", "Test Book", "Description", "index", "BookAuthor1, BookAuthor2", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), 100, "image.jpg", true);

        when(jpaBookRepository.save(any(Book.class))).thenReturn(testBook);

        // when
        BookResponse response = bookService.createBook(request);

        // then
        assertNotNull(response);
        assertEquals(testBook.getBookId(), response.bookId());
        assertEquals(testBook.getBookIsbn(), response.bookIsbn());
        assertEquals(testBook.getBookName(), response.bookName());
    }

    @DisplayName("책 생성 - 실패 (요청 값이 비어있음)")
    @Test
    void createBook_failure_nullRequest() {
        // given
        CreateBookRequest request = null;

        // then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> bookService.createBook(request));
        assertEquals("요청 값이 비어있습니다.", exception.getErrorStatus().message());
    }

    @DisplayName("책 조회 - 성공")
    @Test
    void findBook_success() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // when
        BookResponse response = bookService.getBook(1L);

        // then
        assertNotNull(response);
        assertEquals(testBook.getBookId(), response.bookId());
        assertEquals(testBook.getBookName(), response.bookName());
    }

    @DisplayName("책 조회 - 실패 (존재하지 않는 책)")
    @Test
    void findBook_failure_bookNotFound() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> bookService.getBook(1L));
        assertEquals("요청 값이 비어있습니다.", exception.getErrorStatus().message());
    }

    @DisplayName("모든 책 조회 - 성공")
    @Test
    void findAllBooks() throws ParseException {
        // given
        Book anotherBook = new Book(2L, "0987654321", "Another Book", "Description", "Index", "Publisher",
                sdf.parse("2014-04-02"), new BigDecimal("25.00"), new BigDecimal("19.99"), "another.jpg",
                150, 0, 0, 0, true, false);

        List<Book> books = Arrays.asList(testBook, anotherBook);
        Page<Book> bookPage = new PageImpl<>(books, PageRequest.of(0, 10), books.size());

        when(jpaBookRepository.findByBookIsDeletedFalse(any(Pageable.class))).thenReturn(bookPage);

        // when
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookResponse> responses = bookService.getAllBooks(pageable);

        // then
        assertEquals(2, responses.getContent().size());
        assertEquals(testBook.getBookName(), responses.getContent().get(0).bookName());
        assertEquals(anotherBook.getBookName(), responses.getContent().get(1).bookName());
    }

    @DisplayName("모든 책 조회 - 실패 (페이지 정보 없음)")
    @Test
    void findAllBooks_failure_noPageable() {
        // given
        when(jpaBookRepository.findByBookIsDeletedFalse(any(Pageable.class))).thenReturn(Page.empty());

        // when
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookResponse> responses = bookService.getAllBooks(pageable);

        // then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @DisplayName("책 업데이트 - 성공")
    @Test
    void updateBook_success() throws ParseException {
        // given
        Book existingBook = new Book(1L, "1234567890", "Old Book Name", "Old Description", "Old Index", "Old Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "old_image.jpg", 100, 0, 0, 0, true, false);

        UpdateBookRequest request = new UpdateBookRequest(1L, "0987654321", "New Book Name", "New Description", "New Index", "new Author1, new Author2", "New Publisher",
                sdf.parse("2022-01-01"), new BigDecimal("25.00"), new BigDecimal("19.99"), 150, "new_image.jpg", true);

        existingBook.updateAll(request.toEntity());

        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(jpaBookRepository.save(any(Book.class))).thenReturn(existingBook);

        // when
        BookResponse response = bookService.updateBook(request);

        // then
        assertNotNull(response);
        assertEquals(existingBook.getBookId(), response.bookId());
        assertEquals(existingBook.getBookIsbn(), response.bookIsbn());
        assertEquals(existingBook.getBookName(), response.bookName());
    }

    @DisplayName("책 업데이트 - 실패 (존재하지 않는 책)")
    @Test
    void updateBook_failure_bookNotFound() throws ParseException {
        // given
        UpdateBookRequest request = new UpdateBookRequest(1L, "Updated Book", "Updated Description", "index", "Updated Publisher", "author1", "publisher",
                sdf.parse("2020-01-01"), new BigDecimal("25.00"), new BigDecimal("20.99"), 120, "updated.jpg", true);

        when(jpaBookRepository.existsById(1L)).thenReturn(false);

        // then
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> bookService.updateBook(request));
        assertEquals("알맞은 책을 찾을 수 없습니다.", exception.getErrorStatus().message());
    }

    @DisplayName("책 삭제 - 성공")
    @Test
    void removeBook_success() {
        // given
        Long bookId = 1L;
        Book book = new Book(bookId, "1234567890", "Test Book", "Description", "index", "Publisher",
                null, null, null, null, 0, 0, 0, 0, true, false);

        List<BookCategory> bookCategoryList = new ArrayList<>();
        List bookTagList = new ArrayList<>();
        List cartBookList = new ArrayList<>();
        List bookAuthorList = new ArrayList<>();
        when(jpaBookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(jpaBookCategoryRepository.findByBook(book)).thenReturn(bookCategoryList);
        when(jpaBookTagRepository.findByBook(book)).thenReturn(bookTagList);
        when(jpaCartBookRepository.findByBook(book)).thenReturn(cartBookList);
        when(jpaBookAuthorRepository.findByBook(book)).thenReturn(bookAuthorList);

        // when
        bookService.removeBook(bookId);

        // then
        verify(jpaBookCategoryRepository, times(1)).deleteAll(bookCategoryList);
        verify(jpaBookTagRepository, times(1)).deleteAll(bookTagList);
        verify(jpaCartBookRepository, times(1)).deleteAll(cartBookList);
        verify(jpaBookAuthorRepository, times(1)).deleteAll(bookAuthorList);
    }

    @DisplayName("책 삭제 - 실패 (존재하지 않는 책)")
    @Test
    void removeBook_failure_bookNotFound() {
        // given
        Long bookId = 1L;
        when(jpaBookRepository.findById(bookId)).thenReturn(Optional.empty());

        // then
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> bookService.removeBook(bookId));
        assertEquals("알맞은 책을 찾을 수 없습니다.", exception.getErrorStatus().message());
    }

    @DisplayName("카테고리별 책 조회 - 성공")
    @Test
    void findBookByCategoryId_success() {
        // given
        List<BookCategory> bookCategories = Collections.singletonList(new BookCategory(1L, testBook, testCategory));

        when(jpaCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(jpaBookCategoryRepository.findByCategory(testCategory)).thenReturn(bookCategories);

        // when
        List<BookResponse> responses = bookService.getBookByCategoryId(1L);

        // then
        assertEquals(1, responses.size());
        assertEquals(testBook.getBookId(), responses.get(0).bookId());
        assertEquals(testBook.getBookName(), responses.get(0).bookName());
    }

    @DisplayName("카테고리별 책 조회 - 실패 (존재하지 않는 카테고리)")
    @Test
    void findBookByCategoryId_failure_categoryNotFound() {
        // given
        when(jpaCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> bookService.getBookByCategoryId(1L));
        assertEquals("일치하는 카테고리가 없습니다.", exception.getErrorStatus().message());
    }

    @DisplayName("책 이름으로 조회 - 성공")
    @Test
    void findBookByName_success() {
        // given
        List<Book> books = Collections.singletonList(testBook);

        when(jpaBookRepository.findByBookNameContainingIgnoreCaseAndBookIsDeletedFalse("Test")).thenReturn(books);

        // when
        List<BookCouponResponse> responses = bookService.getBookByName("Test");

        // then
        assertEquals(1, responses.size());
        assertEquals(testBook.getBookId(), responses.get(0).bookId());
        assertEquals(testBook.getBookName(), responses.get(0).bookName());
    }

    @DisplayName("책 주문별 조회 - 성공")
    @Test
    void findBooksByOrder_success() {
        // given
        List<Long> bookIds = Arrays.asList(1L, 2L);
        Book anotherBook = new Book(2L, "0987654321", "Another Book", "Description", "index", "Publisher",
                null, null, null, null, 0, 0, 0, 0, true, false);

        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaBookRepository.findById(2L)).thenReturn(Optional.of(anotherBook));

        // when
        List<BookOrderResponse> responses = bookService.getBooksByOrder(bookIds);

        // then
        assertEquals(2, responses.size());
        assertEquals(testBook.getBookId(), responses.get(0).bookId());
        assertEquals(anotherBook.getBookId(), responses.get(1).bookId());
    }

    @DisplayName("책 주문별 조회 - 실패 (존재하지 않는 책)")
    @Test
    void findBooksByOrder_failure_bookNotFound() {
        // given
        List<Long> bookIds = Arrays.asList(1L, 2L);
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaBookRepository.findById(2L)).thenReturn(Optional.empty());

        // then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> bookService.getBooksByOrder(bookIds));
        assertEquals("책 값이 비어있습니다.", exception.getErrorStatus().message());
    }

    @DisplayName("카테고리별 책 조회 - 성공")
    @Test
    void findBooksByCategoryId_withSorting() {
        // given
        List<BookCategory> bookCategories = Arrays.asList(
                new BookCategory(1L, testBook, testCategory),
                new BookCategory(2L, new Book(2L, "0987654321", "Another Book", "Description", "index", "Publisher",
                        null, null, null, null, 0, 0, 0, 0, true, false), testCategory));

        when(jpaCategoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(jpaBookCategoryRepository.findByCategory(testCategory)).thenReturn(bookCategories);

        // when
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookResponse> responses = bookService.getBookByCategoryId(1L, pageable, "popularity");

        // then
        assertEquals(2, responses.getContent().size());
    }

    @DisplayName("카테고리별 책 조회 - 실패 (존재하지 않는 카테고리)")
    @Test
    void findBooksByCategoryId_withSorting_failure_categoryNotFound() {
        // given
        when(jpaCategoryRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> bookService.getBookByCategoryId(1L, PageRequest.of(0, 10), "popularity"));
        assertEquals("일치하는 카테고리가 없습니다.", exception.getErrorStatus().message());
    }
}