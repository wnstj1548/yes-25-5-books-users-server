package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.LikesService;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateLikesRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateLikesRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.LikesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LikesController {
    private final LikesService likesService;

    @GetMapping("/likes/users/{userId}")
    public ResponseEntity<List<LikesResponse>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(likesService.findLikeByUserId(userId));
    }

    @GetMapping("/likes/books/{bookId}")
    public ResponseEntity<List<LikesResponse>> findByBookId(@PathVariable Long bookId) {
        return ResponseEntity.ok(likesService.findLikeByBookId(bookId));
    }

    @PutMapping("/likes")
    public ResponseEntity<LikesResponse> update(@RequestBody UpdateLikesRequest request) {
        return ResponseEntity.ok(likesService.updateLikeStatus(request));
    }

    @PostMapping("/likes")
    public ResponseEntity<LikesResponse> create(@RequestBody CreateLikesRequest request) {
        return ResponseEntity.ok(likesService.createLike(request));
    }

}
