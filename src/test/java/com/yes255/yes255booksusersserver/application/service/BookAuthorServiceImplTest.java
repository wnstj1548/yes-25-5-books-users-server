package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.BookAuthorServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.persistance.domain.Author;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.BookAuthor;
import com.yes255.yes255booksusersserver.persistance.repository.JpaAuthorRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookAuthorRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookAuthorRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookAuthorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookAuthorServiceImplTest {

    @Mock
    private JpaBookAuthorRepository jpaBookAuthorRepository;

    @Mock
    private JpaBookRepository jpaBookRepository;

    @Mock
    private JpaAuthorRepository jpaAuthorRepository;

    @InjectMocks
    private BookAuthorServiceImpl bookAuthorService;

    private Book testBook;
    private Author testAuthor;
    private BookAuthor testBookAuthor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        testBook = new Book(1L, "Test Book", "description",  "author", "publisher", null, null, null, "image", 10, 0, 0, 0, true, false);
        testAuthor = new Author(1L, "Test Author");
        testBookAuthor = new BookAuthor(1L, testBook, testAuthor);

        // Mocking repository methods
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaAuthorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(jpaBookAuthorRepository.save(any())).thenReturn(testBookAuthor);
        when(jpaBookAuthorRepository.findByBook(testBook)).thenReturn(Collections.singletonList(testBookAuthor));
        when(jpaBookAuthorRepository.findByAuthor(testAuthor)).thenReturn(Collections.singletonList(testBookAuthor));
    }

    @DisplayName("책 ID로 책-작가 관계 조회 - 성공")
    @Test
    void getBookAuthorByBookId_success() {
        // when
        List<BookAuthorResponse> responses = bookAuthorService.getBookAuthorByBookId(1L);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testBook.getBookId(), responses.get(0).bookId());
        assertEquals(testAuthor.getAuthorId(), responses.get(0).authorId());
    }

    @DisplayName("작가 ID로 책-작가 관계 조회 - 성공")
    @Test
    void getBookAuthorByAuthorId_success() {
        // when
        List<BookAuthorResponse> responses = bookAuthorService.getBookAuthorByAuthorId(1L);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testBook.getBookId(), responses.get(0).bookId());
        assertEquals(testAuthor.getAuthorId(), responses.get(0).authorId());
    }

    @DisplayName("책-작가 관계 생성 - 성공")
    @Test
    void createBookAuthor_success() {
        // given
        CreateBookAuthorRequest request = new CreateBookAuthorRequest(1L, 1L);

        // when
        BookAuthorResponse response = bookAuthorService.createBookAuthor(request);

        // then
        assertNotNull(response);
        assertEquals(testBook.getBookId(), response.bookId());
        assertEquals(testAuthor.getAuthorId(), response.authorId());
    }

    @DisplayName("책-작가 관계 생성 - 실패 (존재하지 않는 책)")
    @Test
    void createBookAuthor_failure_bookNotFound() {
        // given
        CreateBookAuthorRequest request = new CreateBookAuthorRequest(2L, 1L);
        when(jpaBookRepository.findById(2L)).thenReturn(Optional.empty());

        // then
        assertThrows(ApplicationException.class, () -> bookAuthorService.createBookAuthor(request));
    }

    @DisplayName("책-작가 관계 생성 - 실패 (존재하지 않는 작가)")
    @Test
    void createBookAuthor_failure_authorNotFound() {
        // given
        CreateBookAuthorRequest request = new CreateBookAuthorRequest(1L, 2L);
        when(jpaAuthorRepository.findById(2L)).thenReturn(Optional.empty());

        // then
        assertThrows(ApplicationException.class, () -> bookAuthorService.createBookAuthor(request));
    }

    @DisplayName("책-작가 관계 삭제 - 성공")
    @Test
    void removeBookAuthor_success() {
        // when
        bookAuthorService.removeBookAuthor(1L);

        // then
        verify(jpaBookAuthorRepository, times(1)).deleteById(1L);
    }
}
