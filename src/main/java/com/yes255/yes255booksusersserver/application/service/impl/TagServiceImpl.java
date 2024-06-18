package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.TagService;
import com.yes255.yes255booksusersserver.persistance.domain.Tag;
import com.yes255.yes255booksusersserver.persistance.repository.JpaTagRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final JpaTagRepository jpaTagRepository;

    public TagResponse toResponse(Tag tag) {
        return TagResponse.builder()
                .tagId(tag.getTagId())
                .tagName(tag.getTagName())
                .build();
    }

    @Transactional
    @Override
    public TagResponse createTag(CreateTagRequest createTagRequest) {

        if(Objects.isNull(createTagRequest)) {
            throw new IllegalArgumentException("createTagRequest must not be null");
        }

        return toResponse(jpaTagRepository.save(createTagRequest.toEntity()));
    }

    @Transactional(readOnly = true)
    @Override
    public TagResponse findTag(Long tagId) {

        if(Objects.isNull(tagId)) {
            throw new IllegalArgumentException("tagId must not be null");
        }

        Tag tag = jpaTagRepository.findById(tagId).orElse(null);

        if(Objects.isNull(tag)) {
            throw new IllegalArgumentException("tagId " + tagId + " not found");
        }

        return toResponse(tag);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TagResponse> findAllTags() {
        return jpaTagRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    @Override
    public TagResponse updateTag(UpdateTagRequest updateTagRequest) {
        if(Objects.isNull(updateTagRequest)) {
            throw new IllegalArgumentException("updateTagRequest must not be null");
        }

        if(!jpaTagRepository.existsById(updateTagRequest.tagId())) {
            throw new IllegalArgumentException("tagId " + updateTagRequest.tagId() + " not found");
        }

        return toResponse(jpaTagRepository.save(updateTagRequest.toEntity()));
    }

    @Transactional
    @Override
    public void deleteTag(Long tagId) {
        if(Objects.isNull(tagId)) {
            throw new IllegalArgumentException("tagId must not be null");
        }

        jpaTagRepository.deleteById(tagId);
    }

}
