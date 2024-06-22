package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.TagServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.persistance.domain.BookTag;
import com.yes255.yes255booksusersserver.persistance.domain.Tag;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookTagRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaTagRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.TagResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TagServiceImplTest {

    @Mock
    private JpaTagRepository jpaTagRepository;

    @Mock
    private JpaBookTagRepository jpaBookTagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(tagService, "jpaTagRepository", jpaTagRepository);
    }

    @DisplayName("태그 생성 - 성공")
    @Test
    void createTag_success() {
        // given
        CreateTagRequest request = new CreateTagRequest("Java");

        Tag tagToSave = new Tag(1L, request.tagName());

        when(jpaTagRepository.save(any(Tag.class))).thenReturn(tagToSave);

        // when
        TagResponse response = tagService.createTag(request);

        // then
        assertNotNull(response);
        assertEquals(tagToSave.getTagId(), response.tagId());
        assertEquals(tagToSave.getTagName(), response.tagName());
    }

    @DisplayName("태그 생성 - 실패 (요청 값이 비어있음)")
    @Test
    void createTag_failure_requestNull() {
        // then
        assertThrows(ApplicationException.class, () -> tagService.createTag(null));
    }

    @DisplayName("태그 조회 - 성공")
    @Test
    void findTag_success() {
        // given
        Long tagId = 1L;
        Tag tag = new Tag(tagId, "Java");

        when(jpaTagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        // when
        TagResponse response = tagService.findTag(tagId);

        // then
        assertNotNull(response);
        assertEquals(tag.getTagId(), response.tagId());
        assertEquals(tag.getTagName(), response.tagName());
    }

    @DisplayName("태그 조회 - 실패 (태그를 찾을 수 없음)")
    @Test
    void findTag_failure_tagNotFound() {
        // given
        Long tagId = 1L;
        when(jpaTagRepository.findById(tagId)).thenReturn(Optional.empty());

        // then
        assertThrows(ApplicationException.class, () -> tagService.findTag(tagId));
    }

    @DisplayName("모든 태그 조회 - 성공")
    @Test
    void findAllTags_success() {
        // given
        List<Tag> tags = List.of(
                new Tag(1L, "Java"),
                new Tag(2L, "Spring"),
                new Tag(3L, "Hibernate")
        );
        when(jpaTagRepository.findAll()).thenReturn(tags);

        // when
        List<TagResponse> responses = tagService.findAllTags();

        // then
        assertNotNull(responses);
        assertEquals(tags.size(), responses.size());
        for (int i = 0; i < tags.size(); i++) {
            assertEquals(tags.get(i).getTagId(), responses.get(i).tagId());
            assertEquals(tags.get(i).getTagName(), responses.get(i).tagName());
        }
    }

    @DisplayName("태그 수정 - 성공")
    @Test
    void updateTag_success() {
        // given
        UpdateTagRequest request = new UpdateTagRequest(1L, "Java Programming");

        Tag existingTag = new Tag(request.tagId(), "Java");

        Tag updatedTag = new Tag(request.tagId(), request.tagName());

        when(jpaTagRepository.existsById(request.tagId())).thenReturn(true);
        when(jpaTagRepository.save(any(Tag.class))).thenReturn(updatedTag);

        // when
        TagResponse response = tagService.updateTag(request);

        // then
        assertNotNull(response);
        assertEquals(updatedTag.getTagId(), response.tagId());
        assertEquals(updatedTag.getTagName(), response.tagName());
    }

    @DisplayName("태그 수정 - 실패 (요청 값이 비어있음)")
    @Test
    void updateTag_failure_requestNull() {
        // then
        assertThrows(ApplicationException.class, () -> tagService.updateTag(null));
    }

    @DisplayName("태그 수정 - 실패 (태그 아이디가 존재하지 않음)")
    @Test
    void updateTag_failure_tagIdNotFound() {
        // given
        UpdateTagRequest request = new UpdateTagRequest(1L, "Java Programming");
        when(jpaTagRepository.existsById(request.tagId())).thenReturn(false);

        // then
        assertThrows(ApplicationException.class, () -> tagService.updateTag(request));
    }

    @DisplayName("태그 삭제 - 성공")
    @Test
    void deleteTag_success() {
        // given
        Long tagId = 1L;
        Tag tag = new Tag(tagId, "Test Tag");

        List<BookTag> bookTagList = new ArrayList<>();

        // Mock 설정
        when(jpaTagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(jpaBookTagRepository.findByTag(tag)).thenReturn(bookTagList);

        // when
        tagService.deleteTag(tagId);

        // then
        verify(jpaTagRepository, times(1)).findById(tagId);
        verify(jpaBookTagRepository, times(1)).findByTag(tag);
        verify(jpaBookTagRepository, times(1)).deleteAll(bookTagList);
        verify(jpaTagRepository, times(1)).deleteById(tagId);
    }

    @DisplayName("태그 삭제 - 실패 (요청 값이 비어있음)")
    @Test
    void deleteTag_failure_tagIdNull() {
        // then
        assertThrows(ApplicationException.class, () -> tagService.deleteTag(null));
    }

}
