package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.CreateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.TagResponse;

import java.util.List;

public interface TagService {

    TagResponse createTag(CreateTagRequest createTagRequest);
    TagResponse findTag(Long tagId);
    List<TagResponse> findAllTags();
    TagResponse updateTag(UpdateTagRequest updateTagRequest);
    void deleteTag(Long tagId);
}
