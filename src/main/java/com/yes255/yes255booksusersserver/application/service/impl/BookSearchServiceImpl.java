package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.BookSearchService;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.domain.index.AuthorIndex;
import com.yes255.yes255booksusersserver.persistance.domain.index.BookIndex;
import com.yes255.yes255booksusersserver.persistance.domain.index.CategoryIndex;
import com.yes255.yes255booksusersserver.persistance.domain.index.TagIndex;
import com.yes255.yes255booksusersserver.persistance.repository.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookIndexResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@Transactional
public class BookSearchServiceImpl implements BookSearchService {

    private final BookElasticSearchRepository bookElasticSearchRepository;
    private final TagElasticSearchRepository tagElasticSearchRepository;
    private final AuthorElasticSearchRepository authorElasticSearchRepository;
    private final CategoryElasticSearchRepository categoryElasticSearchRepository;
    private final JpaBookAuthorRepository jpaBookAuthorRepository;
    private final JpaBookRepository jpaBookRepository;
    private final JpaBookTagRepository jpaBookTagRepository;
    private final JpaTagRepository jpaTagRepository;
    private final JpaAuthorRepository jpaAuthorRepository;
    private final JpaCategoryRepository jpaCategoryRepository;
    private final JpaBookCategoryRepository jpaBookCategoryRepository;

    @Override
    public Page<BookIndexResponse> searchBookByNamePaging(String keyword, Pageable pageable, String sortString) {

        Sort sort = getSort(sortString);

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<BookIndex> result = bookElasticSearchRepository.findByBookNameContainsIgnoreCase(keyword, sortedPageable);

        return result.map(BookIndexResponse::fromIndex);
    }

    @Override
    public Page<BookIndexResponse> searchBookByDescription(String keyword, Pageable pageable, String sortString) {

        Sort sort = getSort(sortString);

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<BookIndex> result = bookElasticSearchRepository.findByBookDescriptionContainsIgnoreCase(keyword, sortedPageable);

        return result.map(BookIndexResponse::fromIndex);
    }

    @Override
    public Page<BookIndexResponse> searchBookByTagName(String keyword, Pageable pageable, String sortString) {

        Sort sort = getSort(sortString);

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<BookIndex> result = bookElasticSearchRepository.findByTagsContainingIgnoreCase(keyword, sortedPageable);

        return result.map(BookIndexResponse::fromIndex);
    }

    @Override
    public Page<BookIndexResponse> searchBookByAuthorName(String keyword, Pageable pageable, String sortString) {

        Sort sort = getSort(sortString);

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<BookIndex> result = bookElasticSearchRepository.findByAuthorsContainingIgnoreCase(keyword, sortedPageable);

        return result.map(BookIndexResponse::fromIndex);
    }

    @Override
    public Page<BookIndexResponse> searchBookByCategoryName(String keyword, Pageable pageable, String sortString) {

        Sort sort = getSort(sortString);

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<BookIndex> result = bookElasticSearchRepository.findByCategoriesContainingIgnoreCase(keyword, sortedPageable);

        return result.map(BookIndexResponse::fromIndex);
    }

    @Override
    public Page<BookIndexResponse> searchAll(String keyword, Pageable pageable, String sortString) {

        Sort sort = getSort(sortString);

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<BookIndex> result = bookElasticSearchRepository.searchAllFields(keyword, sortedPageable);

        return result.map(BookIndexResponse::fromIndex);
    }

    public List<BookIndex> fetchAuthorsAndTags(List<BookIndex> bookIndexList) {

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

            List<CategoryIndex> categories = jpaBookCategoryRepository.findByBook(book).stream()
                            .map(BookCategory::getCategory)
                            .map(CategoryIndex::fromCategory)
                            .toList();

            result.add(BookIndex.updateAuthorsAndTagsAndCategory(bookIndex, authors, tags, categories));
        }

        return result;
    }

    @Scheduled(cron = "0 38 * * * ?")
    public void syncBook() {
        log.info("book sync start");
        LocalDateTime lastBookSyncTime = LocalDateTime.now().minusHours(1);
        List<BookIndex> bookDeletedList = jpaBookRepository.findByBookIsDeletedTrueAndLastModifiedAfter(lastBookSyncTime).stream().map(BookIndex::fromBook).toList();
        bookElasticSearchRepository.deleteAll(bookDeletedList);
        List<BookIndex> bookIndexList = jpaBookRepository.findByBookIsDeletedFalseAndLastModifiedAfter(lastBookSyncTime).stream().map(BookIndex::fromBook).toList();
        bookElasticSearchRepository.saveAll(fetchAuthorsAndTags(bookIndexList));
    }

    @Scheduled(cron = "0 37 * * * ?")
    public void syncTag() {
        log.info("tag sync start");
        LocalDateTime lastTagSyncTime = LocalDateTime.now().minusHours(1);
        List<TagIndex> tagIndexList = jpaTagRepository.findByLastModifiedAfter(lastTagSyncTime).stream().map(TagIndex::fromTag).toList();
        tagElasticSearchRepository.saveAll(tagIndexList);
    }

    @Scheduled(cron = "0 37 * * * ?")
    public void syncAuthor() {
        log.info("author sync start");
        LocalDateTime lastAuthorSyncTime = LocalDateTime.now().minusHours(1);
        List<AuthorIndex> authorIndexList = jpaAuthorRepository.findByLastModifiedAfter(lastAuthorSyncTime).stream().map(AuthorIndex::fromAuthor).toList();
        authorElasticSearchRepository.saveAll(authorIndexList);
    }

    @Scheduled(cron = "0 37 * * * ?")
    public void syncCategory() {
        log.info("category sync start");
        LocalDateTime lastCategorySyncTime = LocalDateTime.now().minusHours(1);
        List<CategoryIndex> categoryIndexList = jpaCategoryRepository.findByLastModifiedAfter(lastCategorySyncTime).stream().map(CategoryIndex::fromCategory).toList();
        categoryElasticSearchRepository.saveAll(categoryIndexList);
    }

    private Sort getSort(String sortString) {

        Sort sort;

        switch(sortString) {
            case "low-price":
                sort = Sort.by(Sort.Order.asc("bookSellingPrice"));
                break;
            case "high-price":
                sort = Sort.by(Sort.Order.desc("bookSellingPrice"));
                break;
            case "review":
                sort = Sort.by(Sort.Order.desc("reviewCount"));
                break;
            case "grade":
                sort = Sort.by(Sort.Order.desc("grade"));
                break;
            case "new-product":
                sort = Sort.by(Sort.Order.desc("publishDate"));
                break;
            case "popularity":
            default:
                sort = Sort.by(Sort.Order.desc("hitsCount"));
                break;
        }

        return sort;
    }
}
