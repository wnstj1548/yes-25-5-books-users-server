package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.BookSearchService;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.domain.index.AuthorIndex;
import com.yes255.yes255booksusersserver.persistance.domain.index.BookIndex;
import com.yes255.yes255booksusersserver.persistance.domain.index.TagIndex;
import com.yes255.yes255booksusersserver.persistance.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    @Override
    public List<BookIndex> searchBookByName(String bookName) {

        List<BookIndex> result = bookElasticSearchRepository.findByBookNameContainsIgnoreCase(bookName);
        log.info("Found {} books", result.size());
        log.info("books : {}", fetchAuthorsAndTags(result));

        return fetchAuthorsAndTags(result);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookIndex> searchBookByDescription(String description) {

        List<BookIndex> result = bookElasticSearchRepository.findByBookDescriptionContainingIgnoreCase(description);

        return fetchAuthorsAndTags(result);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookIndex> searchBookByTagName(String tagName) {

        List<TagIndex> tagIndexList = tagElasticSearchRepository.findByTagName(tagName);
        List<BookIndex> bookIndexList = new ArrayList<>();

        for(TagIndex tagIndex : tagIndexList) {
            BookTag bookTag = jpaBookTagRepository.findById(tagIndex.getTagId())
                    .orElseThrow(() -> new ApplicationException(
                            ErrorStatus.toErrorStatus("해당 태그에 알맞은 도서를 찾을 수 없습니다.", 404, LocalDateTime.now())
                    ));

            Book book = bookTag.getBook();
            BookIndex bookIndex = BookIndex.fromBook(book);

            List<AuthorIndex> authors = jpaBookAuthorRepository.findByBook(book).stream()
                            .map(BookAuthor::getAuthor)
                            .map(AuthorIndex::fromAuthor)
                            .toList();

            bookIndexList.add(BookIndex.updateAuthorsAndTags(bookIndex, authors, tagIndexList));

        }

        return bookIndexList;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookIndex> searchBookByAuthorName(String authorName) {

        List<AuthorIndex> authorIndexList = authorElasticSearchRepository.findByAuthorName(authorName);
        List<BookIndex> bookIndexList = new ArrayList<>();

        for(AuthorIndex authorIndex : authorIndexList) {
            BookAuthor bookAuthor = jpaBookAuthorRepository.findById(authorIndex.getAuthorId())
                    .orElseThrow(() -> new ApplicationException(
                            ErrorStatus.toErrorStatus("해당 작가에 맞는 도서를 찾을 수 없습니다.", 404, LocalDateTime.now())
                    ));

            Book book = bookAuthor.getBook();
            BookIndex bookIndex = BookIndex.fromBook(book);

            List<TagIndex> tags = jpaBookTagRepository.findByBook(book).stream()
                    .map(BookTag::getTag)
                    .map(TagIndex::fromTag)
                    .toList();

            bookIndexList.add(BookIndex.updateAuthorsAndTags(bookIndex, authorIndexList, tags));
        }

        return bookIndexList;
    }

    private List<BookIndex> fetchAuthorsAndTags(List<BookIndex> bookIndexList) {

        List<BookIndex> result = new ArrayList<>();

        for(BookIndex bookIndex : bookIndexList) {
            Book book = jpaBookRepository.findById(bookIndex.getBookId())
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

    @Scheduled(cron = "0 7 * * * ?")
    public void syncBook() {
        log.info("book sync start");
        List<Book> books = jpaBookRepository.findAll();
        for(Book book : books) {
            BookIndex bookIndex = BookIndex.fromBook(book);
            bookElasticSearchRepository.save(bookIndex);
        }
    }

    @Scheduled(cron = "0 16 * * * ?")
    public void syncTag() {
        log.info("tag sync start");
        List<Tag> tags = jpaTagRepository.findAll();
        for(Tag tag : tags) {
            TagIndex tagIndex = TagIndex.fromTag(tag);
            tagElasticSearchRepository.save(tagIndex);
        }
    }

    @Scheduled(cron = "0 15 * * * ?")
    public void syncAuthor() {
        log.info("author sync start");
        List<Author> authors = jpaAuthorRepository.findAll();
        for(Author author : authors) {
            AuthorIndex authorIndex = AuthorIndex.fromAuthor(author);
            authorElasticSearchRepository.save(authorIndex);
        }
    }
}
