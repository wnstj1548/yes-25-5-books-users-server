package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.LikesServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaLikesRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateLikesRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateLikesRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.LikesResponse;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LikesServiceImplTest {

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

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    void setUp() throws ParseException {
        MockitoAnnotations.initMocks(this);

        // Mock data setup
        testUser = User.builder()
                .userEmail("test@gmail.com")
                .userState(new UserState(null, "statename"))
                .userPhone("010-2341-2342")
                .userName("test")
                .userPassword("password")
                .userRegisterDate(LocalDateTime.now())
                .userBirth(null)
                .provider(new Provider(null, "providerName"))
                .customer(new Customer(null, "Role"))
                .build();

        testBook = new Book(1L, "1234567890", "Test Book", "Description", "Index", "Author", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "image.jpg",
                100, 0, 0, 0);

        // Inject mocks into service
        ReflectionTestUtils.setField(likesService, "jpaLikesRepository", jpaLikesRepository);
        ReflectionTestUtils.setField(likesService, "jpaUserRepository", jpaUserRepository);
        ReflectionTestUtils.setField(likesService, "jpaBookRepository", jpaBookRepository);
    }

    @DisplayName("유저 ID로 좋아요 조회 - 성공")
    @Test
    void findLikeByUserId_success() {
        // given
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(jpaLikesRepository.findByUser(any())).thenReturn(List.of(new Likes(null, true, testBook, testUser)));

        // when
        List<LikesResponse> responses = likesService.findLikeByUserId(1L);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testUser.getUserId(), responses.get(0).user().getUserId());
        assertEquals(testBook.getBookId(), responses.get(0).book().getBookId());
        assertTrue(responses.get(0).likesStatus());
    }

    @DisplayName("유저 ID로 좋아요 조회 - 실패 (유저를 찾을 수 없음)")
    @Test
    void findLikeByUserId_failure_userNotFound() {
        // given
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThrows(ApplicationException.class, () -> likesService.findLikeByUserId(1L));
    }

    @DisplayName("책 ID로 좋아요 조회 - 성공")
    @Test
    void findLikeByBookId_success() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaLikesRepository.findByBook(any())).thenReturn(List.of(new Likes(null, true, testBook, testUser)));

        // when
        List<LikesResponse> responses = likesService.findLikeByBookId(1L);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testUser.getUserId(), responses.get(0).user().getUserId());
        assertEquals(testBook.getBookId(), responses.get(0).book().getBookId());
        assertTrue(responses.get(0).likesStatus());
    }

    @DisplayName("책 ID로 좋아요 조회 - 실패 (책을 찾을 수 없음)")
    @Test
    void findLikeByBookId_failure_bookNotFound() {
        // given
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThrows(ApplicationException.class, () -> likesService.findLikeByBookId(1L));
    }

    @DisplayName("좋아요 생성 - 성공")
    @Test
    void createLike_success() {
        // given
        CreateLikesRequest request = new CreateLikesRequest(1L, 1L);
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(jpaLikesRepository.save(any())).thenAnswer(invocation -> {
            Likes likes = invocation.getArgument(0); // Set likesId mock return value
            return likes;
        });

        // when
        LikesResponse response = likesService.createLike(request);

        // then
        assertNotNull(response);
        assertEquals(testUser.getUserId(), response.user().getUserId());
        assertEquals(testBook.getBookId(), response.book().getBookId());
        assertTrue(response.likesStatus());
    }

    @DisplayName("좋아요 생성 - 실패 (책을 찾을 수 없음)")
    @Test
    void createLike_failure_bookNotFound() {
        // given
        CreateLikesRequest request = new CreateLikesRequest(1L, 1L);
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThrows(ApplicationException.class, () -> likesService.createLike(request));
    }

    @DisplayName("좋아요 생성 - 실패 (유저를 찾을 수 없음)")
    @Test
    void createLike_failure_userNotFound() {
        // given
        CreateLikesRequest request = new CreateLikesRequest(1L, 1L);
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThrows(ApplicationException.class, () -> likesService.createLike(request));
    }

    @DisplayName("좋아요 상태 업데이트 - 성공")
    @Test
    void updateLikeStatus_success() {
        // given
        UpdateLikesRequest request = new UpdateLikesRequest(1L, 1L);
        Likes existingLike = new Likes(null, true, testBook, testUser);
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(jpaLikesRepository.findByUserAndBook(testUser, testBook)).thenReturn(Optional.of(existingLike));
        when(jpaLikesRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        LikesResponse response = likesService.updateLikeStatus(request);

        // then
        assertNotNull(response);
        assertEquals(testUser.getUserId(), response.user().getUserId());
        assertEquals(testBook.getBookId(), response.book().getBookId());
        assertFalse(response.likesStatus());
    }

    @DisplayName("좋아요 상태 업데이트 - 실패 (책을 찾을 수 없음)")
    @Test
    void updateLikeStatus_failure_bookNotFound() {
        // given
        UpdateLikesRequest request = new UpdateLikesRequest(1L, 1L);
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThrows(ApplicationException.class, () -> likesService.updateLikeStatus(request));
    }

    @DisplayName("좋아요 상태 업데이트 - 실패 (유저를 찾을 수 없음)")
    @Test
    void updateLikeStatus_failure_userNotFound() {
        // given
        UpdateLikesRequest request = new UpdateLikesRequest(1L, 1L);
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.empty());

        // then
        assertThrows(ApplicationException.class, () -> likesService.updateLikeStatus(request));
    }

    @DisplayName("좋아요 상태 업데이트 - 실패 (좋아요를 찾을 수 없음)")
    @Test
    void updateLikeStatus_failure_likeNotFound() {
        // given
        UpdateLikesRequest request = new UpdateLikesRequest(1L, 1L);
        when(jpaBookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(jpaUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(jpaLikesRepository.findByUserAndBook(testUser, testBook)).thenReturn(Optional.empty());

        // then
        assertThrows(ApplicationException.class, () -> likesService.updateLikeStatus(request));
    }
}

