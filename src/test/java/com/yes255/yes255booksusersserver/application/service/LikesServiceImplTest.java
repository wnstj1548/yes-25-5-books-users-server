package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.LikesServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaLikesRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.response.LikesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LikesServiceImplTest {

    @Mock
    private JpaLikesRepository jpaLikesRepository;

    @Mock
    private JpaUserRepository jpaUserRepository;

    @Mock
    private JpaBookRepository jpaBookRepository;

    @InjectMocks
    private LikesServiceImpl likesService;

    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        likesService = new LikesServiceImpl(jpaLikesRepository, jpaUserRepository, jpaBookRepository);

        testUser = User.builder()
                .userId(1L)
                .customer(null)
                .userName("Test User")
                .userEmail("test@example.com")
                .userPhone("010-1234-5678")
                .userRegisterDate(LocalDateTime.now().minusDays(1))
                .userLastLoginDate(LocalDateTime.now())
                .provider(Provider.builder().providerName("LOCAL").build())
                .userState(UserState.builder().userStateName("ACTIVE").build())
                .userGrade(UserGrade.builder().userGradeName("NORMAL").build())
                .userPassword("encodedPassword")
                .build();

        testBook = Book.builder()
                .bookId(1L)
                .build();
    }

    @DisplayName("유저 ID로 좋아요 조회 - 성공")
    @Test
    void findLikeByUserId_success() {
        // given
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(jpaLikesRepository.findByUserAndLikesStatus(testUser, true)).thenReturn(List.of(new Likes(1L, true, testBook, testUser)));

        // when
        List<LikesResponse> responses = likesService.getLikeByUserId(1L);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testUser.getUserId(), responses.get(0).userId());
        assertEquals(testBook.getBookId(), responses.get(0).bookId());
        assertTrue(responses.get(0).likesStatus());
    }

    @DisplayName("유저 ID로 좋아요 조회 - 실패 (유저를 찾을 수 없음)")
    @Test
    void findLikeByUserId_failure_userNotFound() {
        // given
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> likesService.getLikeByUserId(1L));
        assertEquals(ErrorStatus.toErrorStatus(null, 404, LocalDateTime.now()).message(), exception.getMessage());
    }

    @DisplayName("책 ID로 좋아요 조회 - 성공")
    @Test
    void findLikeByBookId_success() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaLikesRepository.findByBook(testBook)).thenReturn(List.of(new Likes(1L, true, testBook, testUser)));

        // when
        List<LikesResponse> responses = likesService.getLikeByBookId(1L);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testUser.getUserId(), responses.get(0).userId());
        assertEquals(testBook.getBookId(), responses.get(0).bookId());
        assertTrue(responses.get(0).likesStatus());
    }

    @DisplayName("책 ID로 좋아요 조회 - 실패 (책을 찾을 수 없음)")
    @Test
    void findLikeByBookId_failure_bookNotFound() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> likesService.getLikeByBookId(1L));
        assertEquals(ErrorStatus.toErrorStatus(null, 404, LocalDateTime.now()).message(), exception.getMessage());
    }

    @DisplayName("좋아요 생성 - 성공")
    @Test
    void createLike_success() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(jpaLikesRepository.save(any())).thenAnswer(invocation -> {
            Likes likes = invocation.getArgument(0);
            return likes;
        });

        // when
        LikesResponse response = likesService.createLike(1L, 1L);

        // then
        assertNotNull(response);
        assertEquals(testUser.getUserId(), response.userId());
        assertEquals(testBook.getBookId(), response.bookId());
        assertTrue(response.likesStatus());
    }

    @DisplayName("좋아요 생성 - 실패 (책을 찾을 수 없음)")
    @Test
    void createLike_failure_bookNotFound() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> likesService.createLike(1L, 1L));
        assertEquals(ErrorStatus.toErrorStatus(null, 404, LocalDateTime.now()).message(), exception.getMessage());
    }

    @DisplayName("좋아요 생성 - 실패 (유저를 찾을 수 없음)")
    @Test
    void createLike_failure_userNotFound() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> likesService.createLike(1L, 1L));
        assertEquals(ErrorStatus.toErrorStatus(null, 404, LocalDateTime.now()).message(), exception.getMessage());
    }

    @DisplayName("좋아요 상태 업데이트 - 성공")
    @Test
    void updateLikeStatus_success() {
        // given
        Likes existingLike = new Likes(1L, true, testBook, testUser);
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(jpaLikesRepository.findByUserAndBook(testUser, testBook)).thenReturn(Optional.of(existingLike));
        when(jpaLikesRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        LikesResponse response = likesService.updateLikeStatus(1L, 1L);

        // then
        assertNotNull(response);
        assertEquals(testUser.getUserId(), response.userId());
        assertEquals(testBook.getBookId(), response.bookId());
        assertFalse(response.likesStatus());
    }

    @DisplayName("좋아요 상태 업데이트 - 실패 (책을 찾을 수 없음)")
    @Test
    void updateLikeStatus_failure_bookNotFound() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> likesService.updateLikeStatus(1L, 1L));
        assertEquals(ErrorStatus.toErrorStatus(null, 404, LocalDateTime.now()).message(), exception.getMessage());
    }

    @DisplayName("좋아요 상태 업데이트 - 실패 (유저를 찾을 수 없음)")
    @Test
    void updateLikeStatus_failure_userNotFound() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> likesService.updateLikeStatus(1L, 1L));
        assertEquals(ErrorStatus.toErrorStatus(null, 404, LocalDateTime.now()).message(), exception.getMessage());
    }

    @DisplayName("좋아요 상태 업데이트 - 실패 (좋아요를 찾을 수 없음)")
    @Test
    void updateLikeStatus_failure_likeNotFound() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(jpaLikesRepository.findByUserAndBook(testUser, testBook)).thenReturn(Optional.empty());
        // then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> likesService.updateLikeStatus(1L, 1L));
        assertEquals(ErrorStatus.toErrorStatus(null, 404, LocalDateTime.now()).message(), exception.getMessage());
    }

    @DisplayName("책 ID와 유저 ID로 좋아요 존재 여부 확인 - 성공")
    @Test
    void isExistByBookIdAndUserId_true() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(jpaLikesRepository.existsByUserAndBook(testUser, testBook)).thenReturn(true);

        // when
        boolean exists = likesService.isExistByBookIdAndUserId(1L, 1L);

        // then
        assertTrue(exists);
    }

    @DisplayName("책 ID와 유저 ID로 좋아요 조회 - 성공")
    @Test
    void getLikeByBookIdAndUserId_success() {
        // given
        Likes existingLike = new Likes(1L, true, testBook, testUser);
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(jpaLikesRepository.findByUserAndBook(testUser, testBook)).thenReturn(Optional.of(existingLike));

        // when
        LikesResponse response = likesService.getLikeByBookIdAndUserId(1L, 1L);

        // then
        assertNotNull(response);
        assertEquals(testUser.getUserId(), response.userId());
        assertEquals(testBook.getBookId(), response.bookId());
        assertTrue(response.likesStatus());
    }

    @DisplayName("책 ID와 유저 ID로 좋아요 조회 - 실패 (책을 찾을 수 없음)")
    @Test
    void getLikeByBookIdAndUserId_failure_bookNotFound() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> likesService.getLikeByBookIdAndUserId(1L, 1L));
        assertEquals(ErrorStatus.toErrorStatus(null, 404, LocalDateTime.now()).message(), exception.getMessage());
    }

    @DisplayName("책 ID와 유저 ID로 좋아요 조회 - 실패 (유저를 찾을 수 없음)")
    @Test
    void getLikeByBookIdAndUserId_failure_userNotFound() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> likesService.getLikeByBookIdAndUserId(1L, 1L));
        assertEquals(ErrorStatus.toErrorStatus(null, 404, LocalDateTime.now()).message(), exception.getMessage());
    }

    @DisplayName("유저 ID가 null일 때 좋아요 조회 - 실패")
    @Test
    void getLikeByBookIdAndUserId_failure_nullUserId() {
        // then
        ApplicationException exception = assertThrows(ApplicationException.class, () -> likesService.getLikeByBookIdAndUserId(1L, null));
        assertEquals(ErrorStatus.toErrorStatus(null, 400, LocalDateTime.now()).message(), exception.getMessage());
    }
}