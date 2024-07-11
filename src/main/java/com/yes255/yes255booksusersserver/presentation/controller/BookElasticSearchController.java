package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.BookSearchService;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookIndexResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
@Tag(name = "도서 검색 API", description = "Elastic Search를 사용하여 도서를 검색하는 API")
public class BookElasticSearchController {

    private final BookSearchService bookSearchService;

    @Operation(summary = "책 이름 검색", description = "책 이름으로 단어가 포함된 책들을 검색합니다.")
    @Parameter(name = "keyword", description = " 검색하고자 하는 검색어를 포함합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "책 이름으로 검색 성공 (ElasticSearch)", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/searchByName")
    public ResponseEntity<Page<BookIndexResponse>> searchByName(@RequestParam String keyword, Pageable pageable, @RequestParam(defaultValue = "popularity") String sortString) {
        return ResponseEntity.ok(bookSearchService.searchBookByNamePaging(keyword, pageable, sortString));
    }

    @Operation(summary = "책 설명 검색", description = "책 설명으로 설명이 포함된 책들을 검색합니다.")
    @Parameter(name = "keyword", description = " 검색하고자 하는 검색어를 포함합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "책 설명으로 검색 성공 (ElasticSearch)", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/searchByDescription")
    public ResponseEntity<Page<BookIndexResponse>> searchByDescription(@RequestParam String keyword, Pageable pageable, @RequestParam(defaultValue = "popularity") String sortString) {
        return ResponseEntity.ok(bookSearchService.searchBookByDescription(keyword, pageable, sortString));
    }

    @Operation(summary = "태그로 검색", description = "책에 관련된 태그로 책들을 검색합니다.")
    @Parameter(name = "keyword", description = " 검색하고자 하는 검색어를 포함합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "책 태그로 검색 성공 (ElasticSearch)", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/searchByTagName")
    public ResponseEntity<Page<BookIndexResponse>> searchByTagName(@RequestParam String keyword, Pageable pageable, @RequestParam(defaultValue = "popularity") String sortString) {
        return ResponseEntity.ok(bookSearchService.searchBookByTagName(keyword, pageable, sortString));
    }

    @Operation(summary = "작가로 검색", description = "책의 작가로 검색합니다.")
    @Parameter(name = "keyword", description = " 검색하고자 하는 검색어를 포함합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작가로 검색 성공 (ElasticSearch)", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/searchByAuthorName")
    public ResponseEntity<Page<BookIndexResponse>> searchByAuthorName(@RequestParam String keyword, Pageable pageable, @RequestParam(defaultValue = "popularity") String sortString) {
        return ResponseEntity.ok(bookSearchService.searchBookByAuthorName(keyword, pageable, sortString));
    }

    @Operation(summary = "카테고리로 검색", description = "책의 카테고리로 검색합니다.")
    @Parameter(name = "keyword", description = " 검색하고자 하는 검색어를 포함합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리로 검색 성공 (ElasticSearch)", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/searchByCategoryName")
    public ResponseEntity<Page<BookIndexResponse>> searchByCategoryName(@RequestParam String keyword, Pageable pageable, @RequestParam(defaultValue = "popularity") String sortString) {
        return ResponseEntity.ok(bookSearchService.searchBookByCategoryName(keyword, pageable, sortString));
    }

    @Operation(summary = "제목, 설명, 태그, 작가로 검색", description = "책 제목,설명,태그,작가에 검색어가 포함되어있는 책들을 검색합니다.")
    @Parameter(name = "keyword", description = " 검색하고자 하는 검색어를 포함합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "책 이름,제목,설명,태그,작가로 검색 성공 (ElasticSearch)", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/searchAll")
    public ResponseEntity<Page<BookIndexResponse>> searchAll(@RequestParam String keyword, Pageable pageable, @RequestParam(defaultValue = "popularity") String sortString) {
        return ResponseEntity.ok(bookSearchService.searchAll(keyword, pageable, sortString));
    }
}
