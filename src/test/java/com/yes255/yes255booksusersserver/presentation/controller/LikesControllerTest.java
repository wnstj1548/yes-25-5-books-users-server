package com.yes255.yes255booksusersserver.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yes255.yes255booksusersserver.application.service.LikesService;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateLikesRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateLikesRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.LikesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LikesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LikesService likesService;

    @InjectMocks
    private LikesController likesController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private Book testBook;
    private User testUser;

    @BeforeEach
    void setUp() throws ParseException {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(likesController).build();

        testBook = new Book(1L, "1234567890", "Test Book", "Description", "Index", "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "image.jpg",
                100, 0, 0, 0);

        testUser = User.builder()
                .userEmail("test@gmail.com")
//                .userGrade(new UserGrade(null, "grade",null))

                .userState(new UserState(null, "statename"))
                .userPhone("010-2341-2342")
                .userName("test")
                .userPassword("password")
                .userRegisterDate(LocalDateTime.now())
                .userBirth(null)
                .provider(new Provider(null, "providerName"))
                .customer(new Customer(null, "Role"))
                .build();
    }

    @DisplayName("특정 사용자의 좋아요 조회 - 성공")
    @Test
    void findByUserId_success() throws Exception {
        Long userId = 1L;
        List<LikesResponse> mockResponse = Collections.emptyList();
        doReturn(mockResponse).when(likesService).getLikeByUserId(userId);

        mockMvc.perform(get("/likes/users/{userId}", userId))
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

        mockMvc.perform(get("/likes/books/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @DisplayName("좋아요 상태 업데이트 - 성공")
    @Test
    void update_success() throws Exception {
        UpdateLikesRequest request = new UpdateLikesRequest(1L, 1L);
        LikesResponse mockResponse = new LikesResponse(1L, 1L, 1L, false);

        doReturn(mockResponse).when(likesService).updateLikeStatus(any(UpdateLikesRequest.class));

        mockMvc.perform(put("/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @DisplayName("새로운 좋아요 생성 - 성공")
    @Test
    void create_success() throws Exception {
        CreateLikesRequest request = new CreateLikesRequest(1L, 1L);
        LikesResponse mockResponse = new LikesResponse(1L, 1L, 1L, true);

        doReturn(mockResponse).when(likesService).createLike(any(CreateLikesRequest.class));

        mockMvc.perform(post("/likes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
