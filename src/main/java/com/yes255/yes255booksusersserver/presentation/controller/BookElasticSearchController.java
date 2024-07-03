package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.BookSearchService;
import com.yes255.yes255booksusersserver.persistance.domain.index.BookIndex;
import com.yes255.yes255booksusersserver.presentation.dto.request.BookSearchRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookIndexResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookElasticSearchController {

    private final BookSearchService bookSearchService;

    @PostMapping("/books/searchByName")
    public ResponseEntity<Page<BookIndexResponse>> searchByName(@RequestBody BookSearchRequest request, Pageable pageable) {
        return ResponseEntity.ok(bookSearchService.searchBookByNamePaging(request, pageable));
    }

//    @PostMapping("/books/searchByName")
//    public ResponseEntity<List<BookIndexResponse>> searchByName(@RequestBody BookSearchRequest request, Pageable pageable) {
//        return ResponseEntity.ok(bookSearchService.searchBookByName(request));
//    }

    @PostMapping("/books/searchByDescription")
    public ResponseEntity<Page<BookIndexResponse>> searchByDescription(@RequestBody BookSearchRequest request, Pageable pageable) {
        return ResponseEntity.ok(bookSearchService.searchBookByDescription(request, pageable));
    }

    @PostMapping("/books/searchByTagName")
    public ResponseEntity<Page<BookIndexResponse>> searchByTagName(@RequestBody BookSearchRequest request, Pageable pageable) {
        return ResponseEntity.ok(bookSearchService.searchBookByTagName(request, pageable));
    }

    @PostMapping("/books/searchByAuthorName")
    public ResponseEntity<Page<BookIndexResponse>> searchByAuthorName(@RequestBody BookSearchRequest request, Pageable pageable) {
        return ResponseEntity.ok(bookSearchService.searchBookByAuthorName(request, pageable));
    }

    @PostMapping("/books/searchAll")
    public ResponseEntity<Page<BookIndexResponse>> searchAll(@RequestBody BookSearchRequest request, Pageable pageable) {
        return ResponseEntity.ok(bookSearchService.searchAll(request, pageable));
    }
}
