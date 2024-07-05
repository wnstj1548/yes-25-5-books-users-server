//package com.yes255.yes255booksusersserver.presentation.controller;
//
//import com.yes255.yes255booksusersserver.application.service.BookSearchService;
//import com.yes255.yes255booksusersserver.persistance.domain.index.BookIndex;
//import com.yes255.yes255booksusersserver.presentation.dto.request.BookSearchRequest;
//import com.yes255.yes255booksusersserver.presentation.dto.response.BookIndexResponse;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@Tag(name = "도서 검색 API", description = "Elastic Search를 사용하여 도서를 검색하는 API")
//public class BookElasticSearchController {
//
//    private final BookSearchService bookSearchService;
//
//    @Operation(summary = "책 이름 검색", description = "책 이름으로 단어가 포함된 책들을 검색합니다.")
//    @Parameter(name = "request", description = " 검색하고자 하는 검색어를 포함합니다.")
//    @PostMapping("/books/searchByName")
//    public ResponseEntity<Page<BookIndexResponse>> searchByName(@RequestBody BookSearchRequest request, Pageable pageable) {
//        return ResponseEntity.ok(bookSearchService.searchBookByNamePaging(request, pageable));
//    }
//
////    @PostMapping("/books/searchByName")
////    public ResponseEntity<List<BookIndexResponse>> searchByName(@RequestBody BookSearchRequest request, Pageable pageable) {
////        return ResponseEntity.ok(bookSearchService.searchBookByName(request));
////    }
//
//    @Operation(summary = "책 설명 검색", description = "책 설명으로 설명이 포함된 책들을 검색합니다.")
//    @Parameter(name = "request", description = " 검색하고자 하는 검색어를 포함합니다.")
//    @PostMapping("/books/searchByDescription")
//    public ResponseEntity<Page<BookIndexResponse>> searchByDescription(@RequestBody BookSearchRequest request, Pageable pageable) {
//        return ResponseEntity.ok(bookSearchService.searchBookByDescription(request, pageable));
//    }
//
//    @Operation(summary = "태그로 검색", description = "책에 관련된 태그로 책들을 검색합니다.")
//    @Parameter(name = "request", description = " 검색하고자 하는 검색어를 포함합니다.")
//    @PostMapping("/books/searchByTagName")
//    public ResponseEntity<Page<BookIndexResponse>> searchByTagName(@RequestBody BookSearchRequest request, Pageable pageable) {
//        return ResponseEntity.ok(bookSearchService.searchBookByTagName(request, pageable));
//    }
//
//    @Operation(summary = "작가로 검색", description = "책의 작가로 검색합니다.")
//    @Parameter(name = "request", description = " 검색하고자 하는 검색어를 포함합니다.")
//    @PostMapping("/books/searchByAuthorName")
//    public ResponseEntity<Page<BookIndexResponse>> searchByAuthorName(@RequestBody BookSearchRequest request, Pageable pageable) {
//        return ResponseEntity.ok(bookSearchService.searchBookByAuthorName(request, pageable));
//    }
//
//    @Operation(summary = "제목, 설명, 태그, 작가로 검색", description = "책 제목,설명,태그,작가에 검색어가 포함되어있는 책들을 검색합니다.")
//    @Parameter(name = "request", description = " 검색하고자 하는 검색어를 포함합니다.")
//    @PostMapping("/books/searchAll")
//    public ResponseEntity<Page<BookIndexResponse>> searchAll(@RequestBody BookSearchRequest request, Pageable pageable) {
//        return ResponseEntity.ok(bookSearchService.searchAll(request, pageable));
//    }
//}
