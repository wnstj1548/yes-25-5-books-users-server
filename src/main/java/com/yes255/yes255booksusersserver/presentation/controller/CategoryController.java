
package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.CategoryService;
import com.yes255.yes255booksusersserver.common.exception.ValidationFailedException;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateCategoryRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 카테고리 관련 작업을 처리하는 RestController 클래스.
 */
@Tag(name = "카테고리 API", description = "카테고리 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/books/categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 모든 카테고리를 조회합니다.
     *
     * @return ResponseEntity<Page<CategoryResponse>> 형식의 카테고리 목록
     */
    @Operation(summary = "모든 카테고리 조회", description = "등록된 카테고리 페이지 처리하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 카테고리 페이지 조회 성공", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/page")
    public ResponseEntity<Page<CategoryResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageable));
    }

    /**
     * 모든 카테고리를 조회합니다.
     *
     * @return ResponseEntity<List<CategoryResponse>> 형식의 카테고리 목록
     */
    @Operation(summary = "모든 카테고리 조회", description = "등록된 카테고리를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 카테고리 리스트 조회 성공", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    /**
     * 특정 카테고리를 조회합니다.
     *
     * @param categoryId 조회할 카테고리의 ID
     * @return ResponseEntity<CategoryResponse> 형식의 특정 카테고리 정보
     */
    @Operation(summary = "특정 카테고리 조회", description = "categoryId로 특정 카테고리를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "categoryId로 카테고리 검색 성공", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> find(@PathVariable("categoryId") Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategory(categoryId));
    }

    /**
     * 루트 카테고리를 조회합니다.
     *
     * @return ResponseEntity<List<CategoryResponse>> 형식의 루트 카테고리 목록
     */
    @Operation(summary = "루트 카테고리(1단계) 조회", description = "모든 카테고리 중 1단계 카테고리를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "부모 카테고리 검색 성공", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/root")
    public ResponseEntity<List<CategoryResponse>> findRoot() {
        return ResponseEntity.ok(categoryService.getRootCategories());
    }

    /**
     * 특정 부모 카테고리 ID를 가진 자식 카테고리를 조회합니다.
     *
     * @param parentId 부모 카테고리의 ID
     * @return ResponseEntity<List<CategoryResponse>> 형식의 부모 카테고리에 속한 자식 카테고리 목록
     */
    @Operation(summary = "부모 카테고리로 특정 카테고리 조회", description = "parentId를 받아 하위 카테고리를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "부모 카테고리로 특정 카테고리 조회 성공", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<CategoryResponse>> findByParentCategoryId(@PathVariable("parentId") Long parentId) {
        return ResponseEntity.ok(categoryService.getCategoryByParentCategoryId(parentId));
    }


    @Operation(summary = "책으로 해당 카테고리 조회", description = "책 아이디를 받아 해당 책의 모든 카테고리를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "책으로 해당 카테고리 조회 성공", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Long>> findByBookId(@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(categoryService.getCategoryIdByBookId(bookId));
    }

    /**
     * 새로운 카테고리를 생성합니다.
     *
     * @param createCategoryRequest 생성할 카테고리 정보를 담은 CreateCategoryRequest 객체
     * @param bindingResult        유효성 검사 결과를 담은 BindingResult 객체
     * @return ResponseEntity<CategoryResponse> 형식의 생성된 카테고리 정보
     * @throws ValidationFailedException 요청에 유효성 검사 오류가 있는 경우 발생합니다.
     */
    @Operation(summary = "새로운 카테고리 생성", description = "새로운 카테고리를 생성합니다.")
    @Parameter(name = "request", description = "categoryName(카테고리 이름)을 포함합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "새로운 카테고리 생성 성공", content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@RequestBody @Valid CreateCategoryRequest createCategoryRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        return ResponseEntity.ok(categoryService.createCategory(createCategoryRequest));
    }

    /**
     * 기존 카테고리를 업데이트합니다.
     *
     * @param updateCategoryRequest 업데이트할 카테고리 정보를 담은 UpdateCategoryRequest 객체
     * @param bindingResult         유효성 검사 결과를 담은 BindingResult 객체
     * @return ResponseEntity<CategoryResponse> 형식의 업데이트된 카테고리 정보
     * @throws ValidationFailedException 요청에 유효성 검사 오류가 있는 경우 발생합니다.
     */
    @Operation(summary = "기존 카테고리 업데이트", description = "기존 카테고리를 업데이트합니다.")
    @Parameter(name = "request", description = "categoryId(PK), categoryName(카테고리 이름)을 포함합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 업데이트 성공", content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PutMapping
    public ResponseEntity<CategoryResponse> update(@RequestBody @Valid UpdateCategoryRequest updateCategoryRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        return ResponseEntity.ok(categoryService.updateCategory(updateCategoryRequest));
    }

    /**
     * 특정 카테고리를 삭제합니다.
     *
     * @param categoryId 삭제할 카테고리의 ID
     * @return 삭제 성공 여부를 나타내는 ResponseEntity
     */
    @Operation(summary = "기존 카테고리 삭제", description = "categoryId로 기존 카테고리를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(@PathVariable("categoryId") Long categoryId) {

        categoryService.removeCategory(categoryId);

        return ResponseEntity.noContent().build();
    }

}
