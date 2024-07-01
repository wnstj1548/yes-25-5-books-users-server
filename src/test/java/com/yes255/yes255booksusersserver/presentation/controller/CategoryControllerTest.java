package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.CategoryService;
import com.yes255.yes255booksusersserver.common.exception.ValidationFailedException;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CategoryResponse;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @DisplayName("모든 카테고리 조회 - 성공")
    @Test
    void findAll_success() throws Exception {
        // given
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        // when + then
        mockMvc.perform(get("/books/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @DisplayName("특정 카테고리 조회 - 성공")
    @Test
    void find_success() throws Exception {
        // given
        when(categoryService.getCategory(1L)).thenReturn(new CategoryResponse(1L, "Test Category", null, null));

        // when + then
        mockMvc.perform(get("/books/categories/{categoryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.categoryName").value("Test Category"));
    }

    @DisplayName("루트 카테고리 조회 - 성공")
    @Test
    void findRoot_success() throws Exception {
        // given
        when(categoryService.getRootCategories()).thenReturn(Collections.emptyList());

        // when + then
        mockMvc.perform(get("/books/categories/root")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @DisplayName("부모 카테고리 ID로 자식 카테고리 조회 - 성공")
    @Test
    void findByParentCategoryId_success() throws Exception {
        // given
        Long parentId = 1L;
        when(categoryService.getCategoryByParentCategoryId(parentId)).thenReturn(Collections.emptyList());

        // when + then
        mockMvc.perform(get("/books/categories/parent/{parentId}", parentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @DisplayName("카테고리 생성 - 성공")
    @Test
    void create_success() throws Exception {
        // given
        CreateCategoryRequest request = new CreateCategoryRequest("Test Category", null);

        when(categoryService.createCategory(any(CreateCategoryRequest.class))).thenReturn(new CategoryResponse(1L, "Test Category", null, null));

        // when + then
        mockMvc.perform(post("/books/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryName\":\"Test Category\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").exists())
                .andExpect(jsonPath("$.categoryName").value("Test Category"));
    }

    @DisplayName("카테고리 업데이트 - 성공")
    @Test
    void update_success() throws Exception {
        // given
        UpdateCategoryRequest request = new UpdateCategoryRequest(1L, "Updated Test Category", null);

        when(categoryService.updateCategory(any(UpdateCategoryRequest.class))).thenReturn(new CategoryResponse(1L, "Updated Test Category", null, null));

        // when + then
        mockMvc.perform(put("/books/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryId\":1,\"categoryName\":\"Updated Test Category\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").exists())
                .andExpect(jsonPath("$.categoryName").value("Updated Test Category"));
    }

    @DisplayName("카테고리 삭제 - 성공")
    @Test
    void delete_success() throws Exception {
        // given
        Long categoryId = 1L;
        doNothing().when(categoryService).removeCategory(categoryId);

        // when + then
        mockMvc.perform(delete("/books/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}
