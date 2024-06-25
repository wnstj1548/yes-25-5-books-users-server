package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.BookTagServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.BookNotFoundException;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.BookTag;
import com.yes255.yes255booksusersserver.persistance.domain.Tag;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookTagRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaTagRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookTagResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookTagServiceImplTest {

    @Mock
    private JpaBookTagRepository jpaBookTagRepository;

    @Mock
    private JpaBookRepository jpaBookRepository;

    @Mock
    private JpaTagRepository jpaTagRepository;

    @InjectMocks
    private BookTagServiceImpl bookTagService;

    private Book testBook;
    private Tag testTag;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    void setUp() throws ParseException {
        MockitoAnnotations.initMocks(this);

        // Mock data setup
        testBook = new Book(1L, "1234567890", "Test Book", "Description", "Index", "Author", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "image.jpg",
                100, 0, 0, 0);

        testTag = new Tag(1L, "Test Tag");

        // Inject mocks into service
        ReflectionTestUtils.setField(bookTagService, "jpaBookTagRepository", jpaBookTagRepository);
        ReflectionTestUtils.setField(bookTagService, "jpaBookRepository", jpaBookRepository);
        ReflectionTestUtils.setField(bookTagService, "jpaTagRepository", jpaTagRepository);
    }

    @DisplayName("책 ID로 북태그 조회 - 성공")
    @Test
    void findBookTagByBookId_success() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(java.util.Optional.of(testBook));
        when(jpaBookTagRepository.findByBook(any())).thenReturn(List.of(new BookTag(null, testBook, testTag)));

        // when
        List<BookTagResponse> responses = bookTagService.getBookTagByBookId(1L);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testBook.getBookId(), responses.get(0).bookId());
        assertEquals(testTag.getTagId(), responses.get(0).tagId());
    }

    @DisplayName("책 ID로 북태그 조회 - 실패 (책을 찾을 수 없음)")
    @Test
    void findBookTagByBookId_failure_bookNotFound() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // then
        assertThrows(ApplicationException.class, () -> bookTagService.getBookTagByBookId(1L));
    }

    @DisplayName("북태그 생성 - 성공")
    @Test
    void createBookTag_success() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(java.util.Optional.of(testBook));
        when(jpaTagRepository.findById(1L)).thenReturn(java.util.Optional.of(testTag));
        when(jpaBookTagRepository.save(any())).thenReturn(new BookTag(null,testBook, testTag));

        // when
        BookTagResponse response = bookTagService.createBookTag(new CreateBookTagRequest(1L, 1L));

        // then
        assertNotNull(response);
        assertEquals(testBook.getBookId(), response.bookId());
        assertEquals(testTag.getTagId(), response.tagId());
    }

    @DisplayName("북태그 생성 - 실패 (책을 찾을 수 없음)")
    @Test
    void createBookTag_failure_bookNotFound() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // then
        assertThrows(BookNotFoundException.class, () -> bookTagService.createBookTag(new CreateBookTagRequest(1L, 1L)));
    }

    @DisplayName("북태그 생성 - 실패 (태그를 찾을 수 없음)")
    @Test
    void createBookTag_failure_tagNotFound() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(java.util.Optional.of(testBook));
        when(jpaTagRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // then
        assertThrows(ApplicationException.class, () -> bookTagService.createBookTag(new CreateBookTagRequest(1L, 1L)));
    }

    @DisplayName("북태그 삭제 - 성공")
    @Test
    void deleteBookTag_success() {
        // given
        long bookTagId = 1L;
        when(jpaBookTagRepository.existsById(bookTagId)).thenReturn(true);

        // when
        bookTagService.removeBookTag(bookTagId);

        // then
        verify(jpaBookTagRepository, times(1)).deleteById(bookTagId);
    }

    @DisplayName("북태그 삭제 - 실패 (알맞지 않은 북태그 값)")
    @Test
    void deleteBookTag_failure_invalidBookTagId() {
        // given
        long invalidBookTagId = 999L;
        when(jpaBookTagRepository.existsById(invalidBookTagId)).thenReturn(false);

        // then
        assertThrows(ApplicationException.class, () -> bookTagService.removeBookTag(invalidBookTagId));
        verify(jpaBookTagRepository, never()).deleteById(anyLong());
    }
}