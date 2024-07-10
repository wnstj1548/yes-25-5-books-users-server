package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.TagService;
import com.yes255.yes255booksusersserver.common.exception.ValidationFailedException;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.TagResponse;
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
 * 책의 태그 관련 작업을 처리하는 RestController
 */
@Tag(name = "태그 API", description = "태그 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/books/tags")
public class TagController {

    private final TagService tagService;

    /**
     * 모든 태그 목록 페이지로 조회합니다.
     *
     * @return ResponseEntity<Page<TagResponse>> 형식의 모든 태그 목록
     */
    @Operation(summary = "모든 태그 조회", description = "등록된 모든 태그를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 태그 페이지 조회 성공", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/page")
    public ResponseEntity<Page<TagResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(tagService.getAllTags(pageable));
    }

    /**
     * 모든 태그 목록을 조회합니다.
     *
     * @return ResponseEntity<List<TagResponse>> 형식의 모든 태그 목록
     */
    @Operation(summary = "모든 태그 조회", description = "등록된 모든 태그를 페이지형식으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 태그 리스트 조회 성공", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<TagResponse>> findAll() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    /**
     * 특정 태그를 조회합니다.
     *
     * @param tagId 조회할 태그의 ID
     * @return ResponseEntity<TagResponse> 형식의 특정 태그 정보
     */
    @Operation(summary = "특정 태그 조회", description = "등록된 특정 태그를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 태그 검색 성공", content = @Content(schema = @Schema(implementation = TagResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/{tagId}")
    public ResponseEntity<TagResponse> find(@PathVariable Long tagId) {
        return ResponseEntity.ok(tagService.getTag(tagId));
    }

    /**
     * 새로운 태그를 생성합니다.
     *
     * @param createTagRequest 생성할 태그 정보를 담은 CreateTagRequest 객체
     * @return ResponseEntity<TagResponse> 형식의 생성된 태그 정보
     */
    @Operation(summary = "새로운 태그 생성", description = "새로운 태그를 생성합니다.")
    @Parameter(name = "request", description = "tagName(태그 이름) 를 포함합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "새로운 태그 생성 성공", content = @Content(schema = @Schema(implementation = TagResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TagResponse> create(@RequestBody @Valid CreateTagRequest createTagRequest) {
        return ResponseEntity.ok(tagService.createTag(createTagRequest));
    }

    /**
     * 기존의 태그 정보를 업데이트합니다.
     *
     * @author 김준서
     * @param updateTagRequest 업데이트할 태그 정보를 담은 UpdateTagRequest 객체
     * @param bindingResult    데이터 유효성 검사 결과를 담은 BindingResult 객체
     * @return ResponseEntity<TagResponse> 형식의 업데이트된 태그 정보
     */
    @Operation(summary = "기존 태그 업데이트", description = "기존 태그를 업데이트합니다.")
    @Parameter(name = "request", description = "tagId(PK), tagName(태그 이름) 를 포함합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "태그 업데이트 성공", content = @Content(schema = @Schema(implementation = TagResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PutMapping
    public ResponseEntity<TagResponse> update(@RequestBody @Valid UpdateTagRequest updateTagRequest, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        return ResponseEntity.ok(tagService.updateTag(updateTagRequest));
    }

    /**
     * 특정 태그를 삭제합니다.
     *
     * @param tagId 삭제할 태그의 ID
     * @return ResponseEntity<Void> 형식의 응답 (콘텐츠 없음)
     */
    @Operation(summary = "특정 태그 삭제", description = "특정 태그를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "태그 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> delete(@PathVariable Long tagId) {

        tagService.removeTag(tagId);

        return ResponseEntity.noContent().build();
    }
}