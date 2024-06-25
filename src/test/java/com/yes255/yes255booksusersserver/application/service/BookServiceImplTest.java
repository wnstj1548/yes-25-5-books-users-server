package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.BookServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.BookNotFoundException;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.BookCategory;
import com.yes255.yes255booksusersserver.persistance.domain.BookTag;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookCategoryRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookTagRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCategoryRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookRequest;
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

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookServiceImplTest {

    @Mock
    private JpaBookRepository jpaBookRepository;


    @Mock
    private JpaBookTagRepository jpaBookTagRepository;

    @Mock
    private JpaBookCategoryRepository jpaBookCategoryRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    void setUp() throws ParseException {
        MockitoAnnotations.initMocks(this);
        testBook = new Book(1L, "1234567890", "Test Book", "Description", "Index", "Author", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "image.jpg",
                100, 0, 0, 0);
    }

    @DisplayName("책 생성 - 성공")
    @Test
    void createBook_success() throws ParseException {
        // given
        CreateBookRequest request = new CreateBookRequest("1234567890", "Test Book", "Description", "index", "Author", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "image.jpg", 100);

        when(jpaBookRepository.save(any(Book.class))).thenReturn(testBook);

        // when
        BookResponse response = bookService.createBook(request);

        // then
        assertNotNull(response);
        assertEquals(testBook.getBookId(), response.bookId());
        assertEquals(testBook.getBookIsbn(), response.bookIsbn());
        assertEquals(testBook.getBookName(), response.bookName());
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
        assertThrows(ApplicationException.class, () -> bookService.getBook(1L));
    }

    @DisplayName("모든 책 조회")
    @Test
    void findAllBooks() throws ParseException {
        // given
        Book anotherBook = new Book(2L, "0987654321", "Another Book", "Description", "Index", "Author", "Publisher",
                sdf.parse("2014-04-02"), new BigDecimal("25.00"), new BigDecimal("19.99"), "another.jpg",
                150, 0, 0, 0);

        List<Book> books = Arrays.asList(testBook, anotherBook);
        Page<Book> bookPage = new PageImpl<>(books, PageRequest.of(0, 10), books.size());

        when(jpaBookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);

        // when
        Pageable pageable = PageRequest.of(0, 10);
        Page<BookResponse> responses = bookService.getAllBooks(pageable);

        // then
        assertEquals(2, responses.getContent().size());
        assertEquals(testBook.getBookName(), responses.getContent().get(0).bookName());
        assertEquals(anotherBook.getBookName(), responses.getContent().get(1).bookName());
    }

    @DisplayName("책 업데이트 - 성공")
    @Test
    void updateBook_success() throws ParseException {
        // given
        Book existingBook = new Book(1L, "1234567890", "Old Book Name", "Old Description", "Old Index", "Old Author", "Old Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "old_image.jpg", 100, 0, 0, 0);

        UpdateBookRequest request = new UpdateBookRequest(1L, "0987654321", "New Book Name", "New Description", "New Index", "New Author", "New Publisher",
                sdf.parse("2022-01-01"), new BigDecimal("25.00"), new BigDecimal("19.99"), "new_image.jpg", 150);

        existingBook.from(request.toEntity());

        when(jpaBookRepository.findById(1L)).thenReturn(java.util.Optional.of(existingBook));
        when(jpaBookRepository.save(any(Book.class))).thenReturn(existingBook);

        // when
        BookResponse response = bookService.updateBook(request);

        // then
        assertNotNull(response);
        assertEquals(existingBook.getBookId(), response.bookId());
        assertEquals(existingBook.getBookIsbn(), response.bookIsbn());
        assertEquals(existingBook.getBookName(), response.bookName());
    }

    @DisplayName("책 수정 - 실패 (존재하지 않는 책)")
    @Test
    void updateBook_failure_bookNotFound() throws ParseException {
        // given
        UpdateBookRequest request = new UpdateBookRequest(1L, "Updated Book", "Updated Description", "Updated Author","index" ,"Updated Publisher", "publisher",
                sdf.parse("2020-01-01"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120);

        when(jpaBookRepository.existsById(1L)).thenReturn(false);

        // then
        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(request));
    }

    @DisplayName("책 삭제 - 성공")
    @Test
    void deleteBook_success() {
        // given
        Long bookId = 1L;
        Book book = new Book(bookId, "1234567890", "Test Book", "Description", "index", "Author", "Publisher",
                null, null, null, null, 0, 0, 0, 0);

        List<BookCategory> bookCategoryList = new ArrayList<>();
        List<BookTag> bookTagList = new ArrayList<>();

        when(jpaBookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(jpaBookCategoryRepository.findByBook(book)).thenReturn(bookCategoryList);
        when(jpaBookTagRepository.findByBook(book)).thenReturn(bookTagList);

        // when
        bookService.removeBook(bookId);

        // then
        verify(jpaBookRepository, times(1)).findById(bookId);
        verify(jpaBookCategoryRepository, times(1)).findByBook(book);
        verify(jpaBookCategoryRepository, times(1)).deleteAll(bookCategoryList);
        verify(jpaBookTagRepository, times(1)).findByBook(book);
        verify(jpaBookTagRepository, times(1)).deleteAll(bookTagList);
        verify(jpaBookRepository, times(1)).deleteById(bookId);
    }

    @DisplayName("책 삭제 - 실패 (존재하지 않는 책)")
    @Test
    void deleteBook_failure_bookNotFound() {
        // given
        when(jpaBookRepository.existsById(1L)).thenReturn(false);

        // then
        assertThrows(BookNotFoundException.class, () -> bookService.removeBook(1L));
        verify(jpaBookRepository, never()).deleteById(anyLong());
    }

}
