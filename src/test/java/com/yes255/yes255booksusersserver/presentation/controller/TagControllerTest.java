package com.yes255.yes255booksusersserver.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yes255.yes255booksusersserver.application.service.TagService;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.TagResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TagControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tagController).build();
    }

    @DisplayName("모든 태그 조회 - 성공")
    @Test
    void findAll_success() throws Exception {
        List<TagResponse> mockResponse = Collections.emptyList();
        doReturn(mockResponse).when(tagService).findAllTags();

        mockMvc.perform(get("/tags"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @DisplayName("특정 태그 조회 - 성공")
    @Test
    void find_success() throws Exception {
        Long tagId = 1L;
        TagResponse mockResponse = new TagResponse(tagId, "Test Tag");

        doReturn(mockResponse).when(tagService).findTag(tagId);

        mockMvc.perform(get("/tags/{tagId}", tagId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tagId").value(mockResponse.tagId()))
                .andExpect(jsonPath("$.tagName").value(mockResponse.tagName()));
    }

    @DisplayName("태그 생성 - 성공")
    @Test
    void create_success() throws Exception {
        CreateTagRequest request = new CreateTagRequest("New Tag");
        TagResponse mockResponse = new TagResponse(1L, "New Tag");

        doReturn(mockResponse).when(tagService).createTag(any(CreateTagRequest.class));

        mockMvc.perform(post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tagId").value(mockResponse.tagId()))
                .andExpect(jsonPath("$.tagName").value(mockResponse.tagName()));
    }

    @DisplayName("태그 업데이트 - 성공")
    @Test
    void update_success() throws Exception {
        UpdateTagRequest request = new UpdateTagRequest(1L, "Updated Tag");
        TagResponse mockResponse = new TagResponse(request.tagId(), request.tagName());

        doReturn(mockResponse).when(tagService).updateTag(any(UpdateTagRequest.class));

        mockMvc.perform(put("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tagId").value(mockResponse.tagId()))
                .andExpect(jsonPath("$.tagName").value(mockResponse.tagName()));
    }

    @DisplayName("태그 삭제 - 성공")
    @Test
    void delete_success() throws Exception {
        Long tagId = 1L;
        mockMvc.perform(delete("/tags/{tagId}", tagId))
                .andExpect(status().isNoContent());
    }
}