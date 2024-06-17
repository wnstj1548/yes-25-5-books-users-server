package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.TagService;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/tags")
    public ResponseEntity<List<TagResponse>> getAllTags() {
        return ResponseEntity.ok(tagService.findAllTags());
    }

    @GetMapping("/tags/{tagId}")
    public ResponseEntity<TagResponse> getTagById(@PathVariable Long tagId) {
        return ResponseEntity.ok(tagService.findByTagId(tagId));
    }

    @PostMapping("/tags")
    public ResponseEntity<TagResponse> createTag(@RequestBody CreateTagRequest createTagRequest) {
        return ResponseEntity.ok(tagService.createTag(createTagRequest));
    }

    @PatchMapping("/tags")
    public ResponseEntity<TagResponse> updateTag(@RequestBody UpdateTagRequest updateTagRequest) {
        return ResponseEntity.ok(tagService.updateTag(updateTagRequest));
    }

    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long tagId) {
        tagService.deleteByTagId(tagId);
        return ResponseEntity.noContent().build();
    }
}
