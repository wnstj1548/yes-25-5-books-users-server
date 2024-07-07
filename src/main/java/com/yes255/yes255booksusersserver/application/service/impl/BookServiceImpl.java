package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.BookService;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.common.exception.BookNotFoundException;
import com.yes255.yes255booksusersserver.persistance.repository.*;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookCouponResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookOrderResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final JpaBookRepository jpaBookRepository;
    private final JpaCategoryRepository jpaCategoryRepository;
    private final JpaBookCategoryRepository jpaBookCategoryRepository;
    private final JpaBookTagRepository jpaBookTagRepository;
    private final JpaCartBookRepository jpaCartBookRepository;
    private final JpaBookAuthorRepository jpaBookAuthorRepository;

    @Transactional
    @Override
    public BookResponse createBook(CreateBookRequest createBookRequest) {

        if(Objects.isNull(createBookRequest)) {
            throw new ApplicationException(
                    ErrorStatus.toErrorStatus("요청 값이 비어있습니다.", 400, LocalDateTime.now())
            );
        }

        Book book = jpaBookRepository.save(createBookRequest.toEntity());

        return toResponse(book);
    }

    @Transactional(readOnly = true)
    @Override
    public BookResponse getBook(long bookId) {

        Book book = jpaBookRepository.findById(bookId).orElseThrow(() -> new ApplicationException(ErrorStatus.toErrorStatus("요청 값이 비어있습니다.", 400, LocalDateTime.now())));
        if(Objects.isNull(book) || book.isBookIsDeleted()) {
            throw new BookNotFoundException(
                    ErrorStatus.toErrorStatus("알맞은 책을 찾을 수 없습니다.", 400, LocalDateTime.now())
            );
        }

        return toResponse(book);
    }

    @Override
    public List<BookOrderResponse> getBooksByOrder(List<Long> bookIdList) {

        List<BookOrderResponse> bookOrderResponseList = new ArrayList<>();

        for(Long bookId : bookIdList) {
            Book book = jpaBookRepository.findById(bookId).orElseThrow(() -> new ApplicationException(ErrorStatus.toErrorStatus("책 값이 비어있습니다.", 400, LocalDateTime.now())));
            bookOrderResponseList.add(BookOrderResponse.fromEntity(book));
        }

        return bookOrderResponseList;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BookResponse> getAllBooks(Pageable pageable) {

        Page<Book> bookPage = jpaBookRepository.findByBookIsDeletedFalse(pageable);
        List<BookResponse> responses = bookPage.stream().map(this::toResponse).toList();

        return new PageImpl<>(responses, pageable, bookPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BookResponse> getAllBooksSorted(Pageable pageable, String sort) {

        Sort sortOption;

        switch(sort) {
            case "new-product":
                sortOption = Sort.by("bookPublishDate").descending();
                break;
            case "low-price":
                sortOption = Sort.by("bookSellingPrice");
                break;
            case "high-price":
                sortOption = Sort.by("bookSellingPrice").descending();
                break;
//            case "grade" :
//                sortOption = Sort.by("grade").descending();
//                break;
            case "review":
                sortOption = Sort.by("reviewCount").descending();
                break;
            case "popularity":
            default:
                sortOption = Sort.by("hitsCount").descending();
                break;
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortOption);
        Page<Book> bookPage = jpaBookRepository.findByBookIsDeletedFalse(sortedPageable);
        List<BookResponse> responses = bookPage.stream().map(this::toResponse).toList();

        return new PageImpl<>(responses, pageable, bookPage.getTotalElements());
    }

    @Override
    public List<BookResponse> getAllBooks() {
        return jpaBookRepository.findByBookIsDeletedFalse().stream().map(this::toResponse).toList();
    }

    @Transactional
    @Override
    public BookResponse updateBook(UpdateBookRequest updateBookRequest) {

        Book existingBook = jpaBookRepository.findById(updateBookRequest.bookId())
                .orElseThrow(() -> new BookNotFoundException(
                ErrorStatus.toErrorStatus("알맞은 책을 찾을 수 없습니다.", 404, LocalDateTime.now())
                ));

        existingBook.updateAll(updateBookRequest.toEntity());

        return toResponse(existingBook);
    }

    @Transactional
    @Override
    public void removeBook(Long bookId) {

        Book book = jpaBookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(
                        ErrorStatus.toErrorStatus("알맞은 책을 찾을 수 없습니다.", 404, LocalDateTime.now())
                ));

        List<BookCategory> bookCategoryList = jpaBookCategoryRepository.findByBook(book);
        List<BookTag> bookTagList = jpaBookTagRepository.findByBook(book);
        List<CartBook> cartBookList = jpaCartBookRepository.findByBook(book);
        List<BookAuthor> bookAuthorList = jpaBookAuthorRepository.findByBook(book);

        jpaBookCategoryRepository.deleteAll(bookCategoryList);
        jpaBookTagRepository.deleteAll(bookTagList);
        jpaCartBookRepository.deleteAll(cartBookList);
        jpaBookAuthorRepository.deleteAll(bookAuthorList);
        book.delete();

    }

    @Transactional
    @Override
    public List<BookResponse> getBookByCategoryId(Long categoryId) {

        List<BookResponse> bookList = new ArrayList<>();
        Category category = jpaCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApplicationException(
                        ErrorStatus.toErrorStatus("일치하는 카테고리가 없습니다.", 404, LocalDateTime.now())
                ));

        List<BookCategory> bookCategoryList = jpaBookCategoryRepository.findByCategory(category);

        for(BookCategory bookCategory : bookCategoryList) {
            if(!bookCategory.getBook().isBookIsDeleted()) {
                bookList.add(toResponse(bookCategory.getBook()));
            }
        }

        return bookList;
    }

    @Transactional
    @Override
    public Page<BookResponse> getBookByCategoryId(Long categoryId, Pageable pageable, String sortString) {

        Category category = jpaCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApplicationException(
                        ErrorStatus.toErrorStatus("일치하는 카테고리가 없습니다.", 404, LocalDateTime.now())
                ));

        List<BookCategory> bookCategoryList = jpaBookCategoryRepository.findByCategory(category);

        Comparator comparator;

        switch(sortString) {
            case "new-product":
                comparator = Comparator.comparing(BookResponse::bookPublishDate).reversed();
                break;
            case "low-price":
                comparator = Comparator.comparing(BookResponse::bookSellingPrice);
                break;
            case "high-price":
                comparator = Comparator.comparing(BookResponse::bookSellingPrice).reversed();
                break;
//            case "grade" :
//                comparator = Comparator.comparing(BookResponse::bookSellingPrice);
//                break;
            case "review":
                comparator = Comparator.comparingInt(BookResponse::reviewCount).reversed();
                break;
            case "popularity":
            default:
                comparator = Comparator.comparingInt(BookResponse::hitsCount).reversed();
                break;
        }

        List<BookResponse> bookList = bookCategoryList.stream()
                .filter(bookCategory -> !bookCategory.getBook().isBookIsDeleted())
                .map(bookCategory -> toResponse(bookCategory.getBook()))
                .sorted(comparator)
                .toList();


        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), bookList.size());
        List<BookResponse> paginatedList = bookList.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, bookList.size());
    }

    @Override
    public List<BookCouponResponse> getBookByName(String name) {
        return jpaBookRepository.findByBookNameContainingIgnoreCaseAndBookIsDeletedFalse(name)
                .stream().map(this::toBookCouponResponse).toList();
    }

    public BookResponse toResponse(Book book) {

        List<BookAuthor> bookAuthorList = jpaBookAuthorRepository.findByBook(book);

        String authorString = bookAuthorList.stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getAuthorName())
                .collect(Collectors.joining(","));

        return BookResponse.builder()
                .bookId(book.getBookId())
                .bookIsbn(book.getBookIsbn())
                .bookName(book.getBookName())
                .bookDescription(book.getBookDescription())
                .bookAuthor(authorString)
                .bookPublisher(book.getBookPublisher())
                .bookPublishDate(book.getBookPublishDate())
                .bookPrice(book.getBookPrice())
                .bookSellingPrice(book.getBookSellingPrice())
                .bookImage(book.getBookImage())
                .bookQuantity(book.getQuantity())
                .reviewCount(book.getReviewCount())
                .hitsCount(book.getHitsCount())
                .searchCount(book.getSearchCount())
                .bookIsPackable(book.isBookIsPackable())
                .build();
    }

    public BookCouponResponse toBookCouponResponse(Book book) {

        List<BookAuthor> bookAuthorList = jpaBookAuthorRepository.findByBook(book);

        String authorString = bookAuthorList.stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getAuthorName())
                .collect(Collectors.joining(","));

        return BookCouponResponse.builder()
                .bookId(book.getBookId())
                .bookName(book.getBookName())
                .authorName(authorString)
                .bookPublisher(book.getBookPublisher())
                .build();
    }
}
