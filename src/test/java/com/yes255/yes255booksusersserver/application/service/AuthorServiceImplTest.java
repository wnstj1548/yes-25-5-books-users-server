package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.AuthorServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Author;
import com.yes255.yes255booksusersserver.persistance.repository.JpaAuthorRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookAuthorRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateAuthorRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.AuthorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthorServiceImplTest {

    @Mock
    private JpaAuthorRepository jpaAuthorRepository;

    @Mock
    private JpaBookAuthorRepository jpaBookAuthorRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author testAuthor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        testAuthor = new Author(1L, "Test Author");

        // Mocking repository methods
        when(jpaAuthorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
        when(jpaAuthorRepository.findByAuthorName("Test Author")).thenReturn(Optional.of(testAuthor));
        when(jpaAuthorRepository.save(any())).thenReturn(testAuthor);
        when(jpaAuthorRepository.findByAuthorName("Nonexistent Author")).thenReturn(Optional.empty());

        // Mocking deleteAll method of jpaBookAuthorRepository
        doNothing().when(jpaBookAuthorRepository).deleteAll(any());
    }

    @DisplayName("작가 조회 - 성공")
    @Test
    void getAuthor_success() {
        // when
        AuthorResponse response = authorService.getAuthor(1L);

        // then
        assertNotNull(response);
        assertEquals(testAuthor.getAuthorId(), response.authorId());
        assertEquals(testAuthor.getAuthorName(), response.authorName());
    }

    @DisplayName("작가 조회 - 실패 (존재하지 않는 작가)")
    @Test
    void getAuthor_failure_authorNotFound() {
        // given
        when(jpaAuthorRepository.findById(2L)).thenReturn(Optional.empty());

        // then
        assertThrows(ApplicationException.class, () -> authorService.getAuthor(2L));
    }

    @DisplayName("작가 이름으로 조회 - 성공")
    @Test
    void getAuthorByName_success() {
        // when
        AuthorResponse response = authorService.getAuthorByName("Test Author");

        // then
        assertNotNull(response);
        assertEquals(testAuthor.getAuthorId(), response.authorId());
        assertEquals(testAuthor.getAuthorName(), response.authorName());
    }

    @DisplayName("작가 이름으로 조회 - 실패 (존재하지 않는 작가)")
    @Test
    void getAuthorByName_failure_authorNotFound() {
        // then
        assertThrows(ApplicationException.class, () -> authorService.getAuthorByName("Nonexistent Author"));
    }

    @DisplayName("작가 생성 - 성공")
    @Test
    void createAuthor_success() {
        // given
        CreateAuthorRequest request = new CreateAuthorRequest("New Author");

        // when
        AuthorResponse response = authorService.createAuthor(request);

        // then
        assertNotNull(response);
        assertEquals(testAuthor.getAuthorId(), response.authorId());
        assertEquals(testAuthor.getAuthorName(), response.authorName());
    }

    @DisplayName("작가 삭제 - 성공")
    @Test
    void removeAuthor_success() {
        // when
        authorService.removeAuthor(1L);

        // then
        verify(jpaBookAuthorRepository, times(1)).deleteAll(any());
        verify(jpaAuthorRepository, times(1)).deleteById(1L);
    }

    @DisplayName("작가 삭제 - 실패 (존재하지 않는 작가)")
    @Test
    void removeAuthor_failure_authorNotFound() {
        // given
        doThrow(new ApplicationException(ErrorStatus.toErrorStatus("해당 작가를 찾을 수 없습니다.", 404, LocalDateTime.now())))
                .when(jpaAuthorRepository).deleteById(2L);

        // then
        assertThrows(ApplicationException.class, () -> authorService.removeAuthor(2L));
    }

    @DisplayName("작가 이름으로 존재 여부 확인")
    @Test
    void isExistAuthorByName() {
        // when
        boolean result = authorService.isExistAuthorByName("Test Author");

        // then
        assertTrue(result);
    }
}