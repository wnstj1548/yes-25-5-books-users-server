package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.TagService;
import com.yes255.yes255booksusersserver.common.exception.ValidationFailedException;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.TagResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/tags")
    public ResponseEntity<List<TagResponse>> findAll() {
        return ResponseEntity.ok(tagService.findAllTags());
    }

    @GetMapping("/tags/{tagId}")
    public ResponseEntity<TagResponse> find(@PathVariable Long tagId) {
        return ResponseEntity.ok(tagService.findTag(tagId));
    }

    @PostMapping("/tags")
    public ResponseEntity<TagResponse> create(@RequestBody @Valid CreateTagRequest createTagRequest) {
        return ResponseEntity.ok(tagService.createTag(createTagRequest));
    }

    @PutMapping("/tags")
    public ResponseEntity<TagResponse> update(@RequestBody @Valid UpdateTagRequest updateTagRequest, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        return ResponseEntity.ok(tagService.updateTag(updateTagRequest));
    }

    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<Void> delete(@PathVariable Long tagId) {

        tagService.deleteTag(tagId);

        return ResponseEntity.noContent().build();
    }
}