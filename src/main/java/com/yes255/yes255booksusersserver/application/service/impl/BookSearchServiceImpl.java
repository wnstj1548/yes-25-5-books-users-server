package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.BookSearchService;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.domain.index.AuthorIndex;
import com.yes255.yes255booksusersserver.persistance.domain.index.BookIndex;
import com.yes255.yes255booksusersserver.persistance.domain.index.TagIndex;
import com.yes255.yes255booksusersserver.persistance.repository.*;
import com.yes255.yes255booksusersserver.presentation.dto.request.BookSearchRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookIndexResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class BookSearchServiceImpl implements BookSearchService {

    private final BookElasticSearchRepository bookElasticSearchRepository;
    private final TagElasticSearchRepository tagElasticSearchRepository;
    private final AuthorElasticSearchRepository authorElasticSearchRepository;
    private final JpaBookAuthorRepository jpaBookAuthorRepository;
    private final JpaBookRepository jpaBookRepository;
    private final JpaBookTagRepository jpaBookTagRepository;
    private final JpaTagRepository jpaTagRepository;
    private final JpaAuthorRepository jpaAuthorRepository;

    @Override
    public Page<BookIndexResponse> searchBookByNamePaging(BookSearchRequest request, Pageable pageable) {

        Page<BookIndex> result = bookElasticSearchRepository.findByBookNameContainsIgnoreCase(request.keyword(), pageable);

        return result.map(BookIndexResponse::fromIndex);
    }

    @Override
    public List<BookIndexResponse> searchBookByName(BookSearchRequest request) {

        List<BookIndex> result = bookElasticSearchRepository.findByBookNameContainsIgnoreCase(request.keyword());

        return result.stream().map(BookIndexResponse::fromIndex).toList();
    }

    @Override
    public Page<BookIndexResponse> searchBookByDescription(BookSearchRequest request, Pageable pageable) {

        Page<BookIndex> result = bookElasticSearchRepository.findByBookDescriptionContainsIgnoreCase(request.keyword(), pageable);

        return result.map(BookIndexResponse::fromIndex);
    }

    @Override
    public Page<BookIndexResponse> searchBookByTagName(BookSearchRequest request, Pageable pageable) {

        Page<BookIndex> result = bookElasticSearchRepository.findByTagsContainingIgnoreCase(request.keyword(), pageable);

        return result.map(BookIndexResponse::fromIndex);
    }

    @Override
    public Page<BookIndexResponse> searchBookByAuthorName(BookSearchRequest request, Pageable pageable) {

        Page<BookIndex> result = bookElasticSearchRepository.findByAuthorsContainingIgnoreCase(request.keyword(), pageable);

        return result.map(BookIndexResponse::fromIndex);
    }

    @Override
    public Page<BookIndexResponse> searchAll(BookSearchRequest request, Pageable pageable) {

        Page<BookIndex> result = bookElasticSearchRepository.searchAllFields(request.keyword(), pageable);

        return result.map(BookIndexResponse::fromIndex);
    }

    private List<BookIndex> fetchAuthorsAndTags(List<BookIndex> bookIndexList) {

        List<BookIndex> result = new ArrayList<>();

        for(BookIndex bookIndex : bookIndexList) {
            Book book = jpaBookRepository.findById(Long.parseLong(bookIndex.getBookId()))
                    .orElseThrow(() -> new ApplicationException(
                            ErrorStatus.toErrorStatus("해당 책을 찾을 수 없습니다.", 404, LocalDateTime.now()
                            )));

            List<AuthorIndex> authors = jpaBookAuthorRepository.findByBook(book).stream()
                    .map(BookAuthor::getAuthor)
                    .map(AuthorIndex::fromAuthor)
                    .toList();

            List<TagIndex> tags = jpaBookTagRepository.findByBook(book).stream()
                    .map(BookTag::getTag)
                    .map(TagIndex::fromTag)
                    .toList();

            result.add(BookIndex.updateAuthorsAndTags(bookIndex, authors, tags));
        }

        return result;
    }

    @Scheduled(cron = "0 10 * * * ?")
    public void syncBook() {
        log.info("book sync start");
        List<BookIndex> bookDeletedList = jpaBookRepository.findByBookIsDeletedTrue().stream().map(BookIndex::fromBook).toList();
        bookElasticSearchRepository.deleteAll(bookDeletedList);
        List<BookIndex> bookIndexList = jpaBookRepository.findByBookIsDeletedFalse().stream().map(BookIndex::fromBook).toList();
        bookElasticSearchRepository.saveAll(fetchAuthorsAndTags(bookIndexList));
    }

    @Scheduled(cron = "0 21 * * * ?")
    public void syncTag() {
        log.info("tag sync start");
        List<TagIndex> tagIndexList = jpaTagRepository.findAll().stream().map(TagIndex::fromTag).toList();
        tagElasticSearchRepository.saveAll(tagIndexList);
    }

    @Scheduled(cron = "0 21 * * * ?")
    public void syncAuthor() {
        log.info("author sync start");
        List<AuthorIndex> authorIndexList = jpaAuthorRepository.findAll().stream().map(AuthorIndex::fromAuthor).toList();
        authorElasticSearchRepository.saveAll(authorIndexList);
    }
}
