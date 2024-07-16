package com.yes255.yes255booksusersserver.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yes255.yes255booksusersserver.application.service.LikesService;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.LikesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LikesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LikesService likesService;

    @InjectMocks
    private LikesController likesController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private Book testBook;
    private User testUser;
    private JwtUserDetails jwtUserDetails;

    @BeforeEach
    void setUp() throws ParseException {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(likesController).build();

        testBook = new Book(1L, "1234567890", "Test Book", "Description",  "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "image.jpg",
                100, 0, 0, 0, true, false);

        testUser = User.builder()
                .userEmail("test@gmail.com")
                .userGrade(new UserGrade(null, "grade",null))
                .userState(new UserState(null, "statename"))
                .userPhone("010-2341-2342")
                .userName("test")
                .userPassword("password")
                .userRegisterDate(LocalDateTime.now())
                .userBirth(null)
                .provider(new Provider(null, "providerName"))
                .customer(new Customer(null, "Role"))
                .build();

        jwtUserDetails = new JwtUserDetails(testUser.getUserId(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")), "password", "refreshtoken");
    }

    @DisplayName("특정 사용자의 좋아요 조회 - 성공")
    @Test
    void findByUserId_success() throws Exception {
        List<LikesResponse> mockResponse = Collections.emptyList();
        doReturn(mockResponse).when(likesService).getLikeByUserId(testUser.getUserId());

        mockMvc.perform(get("/books/likes/users")
                        .principal(() -> testUser.getUserEmail()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @DisplayName("특정 책에 대한 좋아요 조회 - 성공")
    @Test
    void findByBookId_success() throws Exception {
        Long bookId = 1L;
        List<LikesResponse> mockResponse = Collections.emptyList();
        doReturn(mockResponse).when(likesService).getLikeByBookId(bookId);

        mockMvc.perform(get("/books/likes/books/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @DisplayName("좋아요 생성 및 업데이트 - 성공")
    @Test
    void click_success_create() throws Exception {
        LikesResponse mockResponse = new LikesResponse(1L, testBook.getBookId(), testUser.getUserId(), false);
        doReturn(mockResponse).when(likesService).createLike(testBook.getBookId(), testUser.getUserId());
        when(likesService.isExistByBookIdAndUserId(testBook.getBookId(), testUser.getUserId())).thenReturn(false);

        mockMvc.perform(post("/books/likes/{bookId}", testBook.getBookId())
                        .principal(() -> testUser.getUserEmail()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookId").value(testBook.getBookId()))
                .andExpect(jsonPath("$.userId").value(testUser.getUserId()));
    }

    @DisplayName("좋아요 생성 및 업데이트 - 성공(업데이트)")
    @Test
    void click_success_update() throws Exception {
        LikesResponse mockResponse = new LikesResponse(1L, testBook.getBookId(), testUser.getUserId(), false);
        doReturn(mockResponse).when(likesService).updateLikeStatus(testBook.getBookId(), testUser.getUserId());
        when(likesService.isExistByBookIdAndUserId(testBook.getBookId(), testUser.getUserId())).thenReturn(true);

        mockMvc.perform(post("/books/likes/{bookId}", testBook.getBookId())
                        .principal(() -> testUser.getUserEmail()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookId").value(testBook.getBookId()))
                .andExpect(jsonPath("$.userId").value(testUser.getUserId()));
    }

    @DisplayName("책과 유저의 좋아요 조회 - 성공")
    @Test
    void findByBookIdAndUserId_success() throws Exception {
        LikesResponse mockResponse = new LikesResponse(1L, testBook.getBookId(), testUser.getUserId(), true);
        doReturn(mockResponse).when(likesService).getLikeByBookIdAndUserId(testBook.getBookId(), testUser.getUserId());
        when(likesService.isExistByBookIdAndUserId(testBook.getBookId(), testUser.getUserId())).thenReturn(true);

        mockMvc.perform(get("/books/likes/{bookId}", testBook.getBookId())
                        .principal(() -> testUser.getUserEmail()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bookId").value(testBook.getBookId()))
                .andExpect(jsonPath("$.userId").value(testUser.getUserId()));
    }
}