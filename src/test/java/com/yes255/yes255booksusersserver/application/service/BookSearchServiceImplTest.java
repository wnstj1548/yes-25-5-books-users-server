package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.BookSearchServiceImpl;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.domain.index.AuthorIndex;
import com.yes255.yes255booksusersserver.persistance.domain.index.BookIndex;
import com.yes255.yes255booksusersserver.persistance.domain.index.CategoryIndex;
import com.yes255.yes255booksusersserver.persistance.domain.index.TagIndex;
import com.yes255.yes255booksusersserver.persistance.repository.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookIndexResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookSearchServiceImplTest {

    @Mock
    private BookElasticSearchRepository bookElasticSearchRepository;

    @Mock
    private JpaBookRepository jpaBookRepository;

    @Mock
    private JpaCategoryRepository jpaCategoryRepository;

    @Mock
    private TagElasticSearchRepository tagElasticSearchRepository;

    @Mock
    private JpaTagRepository jpaTagRepository;

    @Mock
    private AuthorElasticSearchRepository authorElasticSearchRepository;

    @Mock
    private JpaAuthorRepository jpaAuthorRepository;

    @Mock
    private CategoryElasticSearchRepository categoryElasticSearchRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookSearchServiceImpl bookSearchService;

    private BookIndex bookIndex;
    private Book book;
    private Book book1;
    private AuthorIndex authorIndex;
    private TagIndex tagIndex;
    private CategoryIndex categoryIndex;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    void setUp() throws ParseException {
        MockitoAnnotations.openMocks(this);

        book = new Book(1L, "1234567890", "Test Book", "Description",  "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "image.jpg",
                100, 0, 0, 0, true, false);
        book1 = new Book(2L, "1234567890", "Test Book", "Description",  "Publisher",
                sdf.parse("2020-01-01"), new BigDecimal("20.00"), new BigDecimal("15.99"), "image.jpg",
                100, 0, 0, 0, true, false);
    }

    @DisplayName("책 이름으로 검색 테스트")
    @Test
    void searchBookByNamePaging() {
        // given
        String keyword = "test";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("bookSellingPrice")));
        List<BookIndex> bookIndices = List.of(new BookIndex());
        Page<BookIndex> page = new PageImpl<>(bookIndices, pageable, bookIndices.size());

        when(bookElasticSearchRepository.findByBookNameContainsIgnoreCase(keyword, pageable)).thenReturn(page);

        // when
        Page<BookIndexResponse> result = bookSearchService.searchBookByNamePaging(keyword, pageable, "low-price");

        // then
        assertEquals(1, result.getTotalElements());
        verify(bookElasticSearchRepository).findByBookNameContainsIgnoreCase(keyword, pageable);
    }

    @DisplayName("책 설명으로 검색 테스트")
    @Test
    void searchBookByDescription() {
        // given
        String keyword = "description";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("bookSellingPrice")));
        List<BookIndex> bookIndices = List.of(new BookIndex());
        Page<BookIndex> page = new PageImpl<>(bookIndices, pageable, bookIndices.size());

        when(bookElasticSearchRepository.findByBookDescriptionContainsIgnoreCase(keyword, pageable)).thenReturn(page);

        // when
        Page<BookIndexResponse> result = bookSearchService.searchBookByDescription(keyword, pageable, "high-price");

        // then
        assertEquals(1, result.getTotalElements());
        verify(bookElasticSearchRepository).findByBookDescriptionContainsIgnoreCase(keyword, pageable);
    }

    @DisplayName("태그 이름으로 검색 테스트")
    @Test
    void searchBookByTagName() {
        // given
        String keyword = "tag";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("hitsCount")));
        List<BookIndex> bookIndices = List.of(new BookIndex());
        Page<BookIndex> page = new PageImpl<>(bookIndices, pageable, bookIndices.size());

        when(bookElasticSearchRepository.findByTagsContainingIgnoreCase(keyword, pageable)).thenReturn(page);

        // when
        Page<BookIndexResponse> result = bookSearchService.searchBookByTagName(keyword, pageable, "popularity");

        // then
        assertEquals(1, result.getTotalElements());
        verify(bookElasticSearchRepository).findByTagsContainingIgnoreCase(keyword, pageable);
    }

    @DisplayName("작가 이름으로 검색 테스트")
    @Test
    void searchBookByAuthorName() {
        // given
        String keyword = "author";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("hitsCount")));
        List<BookIndex> bookIndices = List.of(new BookIndex());
        Page<BookIndex> page = new PageImpl<>(bookIndices, pageable, bookIndices.size());

        when(bookElasticSearchRepository.findByAuthorsContainingIgnoreCase(keyword, pageable)).thenReturn(page);

        // when
        Page<BookIndexResponse> result = bookSearchService.searchBookByAuthorName(keyword, pageable, "popularity");

        // then
        assertEquals(1, result.getTotalElements());
        verify(bookElasticSearchRepository).findByAuthorsContainingIgnoreCase(keyword, pageable);
    }

    @DisplayName("카테고리 이름으로 검색 테스트")
    @Test
    void searchBookByCategoryName() {
        // given
        String keyword = "category";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("hitsCount")));
        List<BookIndex> bookIndices = List.of(new BookIndex());
        Page<BookIndex> page = new PageImpl<>(bookIndices, pageable, bookIndices.size());

        when(bookElasticSearchRepository.findByCategoriesContainingIgnoreCase(keyword, pageable)).thenReturn(page);

        // when
        Page<BookIndexResponse> result = bookSearchService.searchBookByCategoryName(keyword, pageable, "popularity");

        // then
        assertEquals(1, result.getTotalElements());
        verify(bookElasticSearchRepository).findByCategoriesContainingIgnoreCase(keyword, pageable);
    }

    @DisplayName("모든 필드로 검색 테스트")
    @Test
    void searchAll() {
        // given
        String keyword = "all";
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("hitsCount")));
        List<BookIndex> bookIndices = List.of(new BookIndex());
        Page<BookIndex> page = new PageImpl<>(bookIndices, pageable, bookIndices.size());

        when(bookElasticSearchRepository.searchAllFields(keyword, pageable)).thenReturn(page);

        // when
        Page<BookIndexResponse> result = bookSearchService.searchAll(keyword, pageable, "popularity");

        // then
        assertEquals(1, result.getTotalElements());
        verify(bookElasticSearchRepository).searchAllFields(keyword, pageable);
    }


    @DisplayName("syncTag 메서드 테스트")
    @Test
    void syncTag() {
        when(jpaTagRepository.findAll()).thenReturn(Arrays.asList(new Tag(1L,"Test Tag")));

        bookSearchService.syncTag();

        verify(tagElasticSearchRepository, times(1)).saveAll(anyList());
    }

    @DisplayName("syncAuthor 메서드 테스트")
    @Test
    void syncAuthor() {
        when(jpaAuthorRepository.findAll()).thenReturn(Arrays.asList(new Author(1L,"Test Author")));

        bookSearchService.syncAuthor();

        verify(authorElasticSearchRepository, times(1)).saveAll(anyList());
    }

    @DisplayName("syncCategory 메서드 테스트")
    @Test
    void syncCategory() {
        when(jpaCategoryRepository.findAll()).thenReturn(Arrays.asList( new Category(1L, "Test Category", null, null)));

        bookSearchService.syncCategory();

        verify(categoryElasticSearchRepository, times(1)).saveAll(anyList());
    }
}
