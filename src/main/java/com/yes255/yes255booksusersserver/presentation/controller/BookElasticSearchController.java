package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.BookSearchService;
import com.yes255.yes255booksusersserver.persistance.domain.index.BookIndex;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookElasticSearchController {

    private final BookSearchService bookSearchService;

    @GetMapping("/books/searchByName")
    public ResponseEntity<List<BookIndex>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(bookSearchService.searchBookByName(name));
    }

    @GetMapping("/books/searchByDescription")
    public ResponseEntity<List<BookIndex>> searchByDescription(@RequestParam String description) {
        return ResponseEntity.ok(bookSearchService.searchBookByDescription(description));
    }

    @GetMapping("/books/searchByTagName")
    public ResponseEntity<List<BookIndex>> searchByTagName(@RequestParam String tagName) {
        return ResponseEntity.ok(bookSearchService.searchBookByTagName(tagName));
    }

    @GetMapping("/books/searchByAuthorName")
    public ResponseEntity<List<BookIndex>> searchByAuthorName(@RequestParam String authorName) {
        return ResponseEntity.ok(bookSearchService.searchBookByAuthorName(authorName));
    }

    @GetMapping("/books/searchAll")
    public ResponseEntity<List<BookIndex>> searchAll(@RequestParam String keyword) {

        List<BookIndex> bookIndexList = new ArrayList<>();

        bookIndexList.addAll(bookSearchService.searchBookByAuthorName(keyword));
        bookIndexList.addAll(bookSearchService.searchBookByTagName(keyword));
        bookIndexList.addAll(bookSearchService.searchBookByDescription(keyword));
        bookIndexList.addAll(bookSearchService.searchBookByName(keyword));

        return ResponseEntity.ok(bookIndexList);
    }
}
