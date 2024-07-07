package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.BookSearchService;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookIndexResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Parameter(name = "request", description = " 검색하고자 하는 검색어를 포함합니다.")
    @GetMapping("/searchByName")
    public ResponseEntity<Page<BookIndexResponse>> searchByName(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(bookSearchService.searchBookByNamePaging(keyword, pageable));
    }

    @Operation(summary = "책 설명 검색", description = "책 설명으로 설명이 포함된 책들을 검색합니다.")
    @Parameter(name = "request", description = " 검색하고자 하는 검색어를 포함합니다.")
    @GetMapping("/searchByDescription")
    public ResponseEntity<Page<BookIndexResponse>> searchByDescription(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(bookSearchService.searchBookByDescription(keyword, pageable));
    }

    @Operation(summary = "태그로 검색", description = "책에 관련된 태그로 책들을 검색합니다.")
    @Parameter(name = "request", description = " 검색하고자 하는 검색어를 포함합니다.")
    @GetMapping("/searchByTagName")
    public ResponseEntity<Page<BookIndexResponse>> searchByTagName(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(bookSearchService.searchBookByTagName(keyword, pageable));
    }

    @Operation(summary = "작가로 검색", description = "책의 작가로 검색합니다.")
    @Parameter(name = "request", description = " 검색하고자 하는 검색어를 포함합니다.")
    @GetMapping("/searchByAuthorName")
    public ResponseEntity<Page<BookIndexResponse>> searchByAuthorName(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(bookSearchService.searchBookByAuthorName(keyword, pageable));
    }

    @Operation(summary = "카테고리로 검색", description = "책의 카테고리로 검색합니다.")
    @Parameter(name = "request", description = " 검색하고자 하는 검색어를 포함합니다.")
    @GetMapping("/searchByCategoryName")
    public ResponseEntity<Page<BookIndexResponse>> searchByCategoryName(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(bookSearchService.searchBookByCategoryName(keyword, pageable));
    }

    @Operation(summary = "제목, 설명, 태그, 작가로 검색", description = "책 제목,설명,태그,작가에 검색어가 포함되어있는 책들을 검색합니다.")
    @Parameter(name = "request", description = " 검색하고자 하는 검색어를 포함합니다.")
    @GetMapping("/searchAll")
    public ResponseEntity<Page<BookIndexResponse>> searchAll(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(bookSearchService.searchAll(keyword, pageable));
    }
}
