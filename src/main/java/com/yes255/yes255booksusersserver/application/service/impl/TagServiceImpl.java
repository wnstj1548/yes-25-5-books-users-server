package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.TagService;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Tag;
import com.yes255.yes255booksusersserver.persistance.repository.JpaTagRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
            throw new ApplicationException(ErrorStatus.toErrorStatus("요청 값이 비어있습니다.", 400, LocalDateTime.now()));
        }

        return toResponse(jpaTagRepository.save(createTagRequest.toEntity()));
    }

    @Transactional(readOnly = true)
    @Override
    public TagResponse findTag(Long tagId) {

        if(Objects.isNull(tagId)) {
            throw new ApplicationException(ErrorStatus.toErrorStatus("요청 값이 비어있습니다.", 400, LocalDateTime.now()));
        }

        Tag tag = jpaTagRepository.findById(tagId).orElseThrow(() -> new ApplicationException(ErrorStatus.toErrorStatus("태그를 찾을 수 없습니다.", 404, LocalDateTime.now())));

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
            throw new ApplicationException(ErrorStatus.toErrorStatus("요청 값이 비어있습니다.", 400, LocalDateTime.now()));
        }

        if(!jpaTagRepository.existsById(updateTagRequest.tagId())) {
            throw new ApplicationException(ErrorStatus.toErrorStatus("태그 아이디가 존재하지 않습니다..", 404, LocalDateTime.now()));
        }

        return toResponse(jpaTagRepository.save(updateTagRequest.toEntity()));
    }

    @Transactional
    @Override
    public void deleteTag(Long tagId) {
        if(Objects.isNull(tagId)) {
            throw new ApplicationException(ErrorStatus.toErrorStatus("요청 값이 비어있습니다.", 400, LocalDateTime.now()));
        }

        jpaTagRepository.deleteById(tagId);
    }

}
