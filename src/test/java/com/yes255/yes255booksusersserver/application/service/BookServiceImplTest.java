package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.BookServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.BookNotFoundException;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
        BookResponse response = bookService.findBook(1L);

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
        assertThrows(ApplicationException.class, () -> bookService.findBook(1L));
    }

    @DisplayName("모든 책 조회")
    @Test
    void findAllBooks() throws ParseException {
        // given
        Book anotherBook = new Book(2L, "0987654321", "Another Book", "Description", "Index", "Author", "Publisher",
                sdf.parse("2014-04-02"), new BigDecimal("25.00"), new BigDecimal("19.99"), "another.jpg",
                150, 0, 0, 0);
        when(jpaBookRepository.findAll()).thenReturn(Arrays.asList(testBook, anotherBook));

        // when
        List<BookResponse> responses = bookService.findAllBooks();

        // then
        assertEquals(2, responses.size());
        assertEquals(testBook.getBookName(), responses.get(0).bookName());
        assertEquals(anotherBook.getBookName(), responses.get(1).bookName());
    }

    @DisplayName("책 수정 - 성공")
    @Test
    void updateBook_success() throws ParseException {
        // given
        UpdateBookRequest request = new UpdateBookRequest(1L, "Updated ISBN", "Updated Name", "Updated Description", "index" ,"Updated Author", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120,0,0,0);
        Book updatedBook = new Book(1L, "Updated ISBN", "Updated Name", "Updated Description", "Index","Updated Author", "Updated Publisher",
                sdf.parse("2000-06-14"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120,0,0,0);

        when(jpaBookRepository.existsById(1L)).thenReturn(true);
        when(jpaBookRepository.save(any(Book.class))).thenReturn(updatedBook);

        // when
        BookResponse response = bookService.updateBook(request);

        // then
        assertNotNull(response);
        assertEquals(testBook.getBookId(), response.bookId());
        assertEquals(request.bookName(), response.bookName());
        assertEquals(request.bookDescription(), response.bookDescription());
        assertEquals(request.bookAuthor(), response.bookAuthor());
        assertEquals(request.bookPublisher(), response.bookPublisher());
    }

    @DisplayName("책 수정 - 실패 (존재하지 않는 책)")
    @Test
    void updateBook_failure_bookNotFound() throws ParseException {
        // given
        UpdateBookRequest request = new UpdateBookRequest(1L, "Updated Book", "Updated Description", "Updated Author","index" ,"Updated Publisher", "publisher",
                sdf.parse("2020-01-01"), new BigDecimal("25.00"), new BigDecimal("20.99"), "updated.jpg", 120,0,0,0);

        when(jpaBookRepository.existsById(1L)).thenReturn(false);

        // then
        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(request));
    }

    @DisplayName("책 삭제 - 성공")
    @Test
    void deleteBook_success() {
        // given
        when(jpaBookRepository.existsById(1L)).thenReturn(true);

        // when
        bookService.deleteBook(1L);

        // then
        verify(jpaBookRepository, times(1)).deleteById(1L);
    }

    @DisplayName("책 삭제 - 실패 (존재하지 않는 책)")
    @Test
    void deleteBook_failure_bookNotFound() {
        // given
        when(jpaBookRepository.existsById(1L)).thenReturn(false);

        // then
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L));
        verify(jpaBookRepository, never()).deleteById(anyLong());
    }

}
