package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.BookService;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.common.exception.BookNotFoundException;
import com.yes255.yes255booksusersserver.persistance.repository.*;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookCategoryResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final JpaBookRepository jpaBookRepository;
    private final JpaCategoryRepository jpaCategoryRepository;
    private final JpaBookCategoryRepository jpaBookCategoryRepository;
    private final JpaBookTagRepository jpaBookTagRepository;
    private final JpaCartBookRepository jpaCartBookRepository;

    public BookResponse toResponse(Book book) {
        return BookResponse.builder()
                .bookId(book.getBookId())
                .bookIsbn(book.getBookIsbn())
                .bookName(book.getBookName())
                .bookDescription(book.getBookDescription())
                .bookAuthor(book.getBookAuthor())
                .bookPublisher(book.getBookPublisher())
                .bookPublishDate(book.getBookPublishDate())
                .bookPrice(book.getBookPrice())
                .bookSellingPrice(book.getBookSellingPrice())
                .bookImage(book.getBookImage())
                .bookQuantity(book.getQuantity())
                .reviewCount(book.getReviewCount())
                .hitsCount(book.getHitsCount())
                .searchCount(book.getSearchCount())
                .build();
    }

    @Transactional
    @Override
    public BookResponse createBook(CreateBookRequest createBookRequest) {

        if(Objects.isNull(createBookRequest)) {
            throw new ApplicationException(ErrorStatus.toErrorStatus("요청 값이 비어있습니다.", 400, LocalDateTime.now()));
        }

        Book book = jpaBookRepository.save(createBookRequest.toEntity());

        return toResponse(book);
    }

    @Transactional(readOnly = true)
    @Override
    public BookResponse findBook(long bookId) {

        Book book = jpaBookRepository.findById(bookId).orElseThrow(() -> new ApplicationException(ErrorStatus.toErrorStatus("요청 값이 비어있습니다.", 400, LocalDateTime.now())));
        if(Objects.isNull(book)) {
            throw new BookNotFoundException(ErrorStatus.toErrorStatus("알맞은 책을 찾을 수 없습니다.", 400, LocalDateTime.now()));
        }

        return toResponse(book);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookResponse> findAllBooks() {
        return jpaBookRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    @Override
    public BookResponse updateBook(UpdateBookRequest updateBookRequest) {

        Book existingBook = jpaBookRepository.findById(updateBookRequest.bookId()).orElseThrow(() -> new BookNotFoundException(ErrorStatus.toErrorStatus("알맞은 책을 찾을 수 없습니다.", 404, LocalDateTime.now())));
        existingBook.from(updateBookRequest.toEntity());

        return toResponse(existingBook);
    }

    @Transactional
    @Override
    public void deleteBook(Long bookId) {

        Book book = jpaBookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(ErrorStatus.toErrorStatus("알맞은 책을 찾을 수 없습니다.", 404, LocalDateTime.now())));

        List<BookCategory> bookCategoryList = jpaBookCategoryRepository.findByBook(book);
        List<BookTag> bookTagList = jpaBookTagRepository.findByBook(book);

        jpaBookCategoryRepository.deleteAll(bookCategoryList);
        jpaBookTagRepository.deleteAll(bookTagList);
        jpaBookRepository.deleteById(bookId);

    }

    @Override
    public List<BookResponse> findBookByCategoryId(Long categoryId) {

        List<BookResponse> bookList = new ArrayList<>();
        Category category = jpaCategoryRepository.findById(categoryId).orElseThrow(() -> new ApplicationException(ErrorStatus.toErrorStatus("일치하는 카테고리가 없습니다.", 404, LocalDateTime.now())));
        List<BookCategory> bookCategoryList = jpaBookCategoryRepository.findByCategory(category);

        for(BookCategory bookCategory : bookCategoryList) {
            bookList.add(toResponse(bookCategory.getBook()));
        }

        return bookList;
    }

}
