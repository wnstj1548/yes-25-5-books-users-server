package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.CreateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.TagResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {

    TagResponse createTag(CreateTagRequest createTagRequest);
    TagResponse findTag(Long tagId);
    List<TagResponse> findAllTags();
    Page<TagResponse> findAllTags(Pageable pageable);
    TagResponse updateTag(UpdateTagRequest updateTagRequest);
    void deleteTag(Long tagId);
}
