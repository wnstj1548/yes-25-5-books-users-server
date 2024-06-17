package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.BookService;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.exception.BookNotFoundException;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final JpaBookRepository jpaBookRepository;

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
                .viewCount(book.getHitsCount())
                .searchCount(book.getSearchCount())
                .build();
    }

    @Transactional
    @Override
    public BookResponse createBook(CreateBookRequest createBookRequest) {

        if(Objects.isNull(createBookRequest)) {
            throw new IllegalArgumentException();
        }

        Book book = jpaBookRepository.save(createBookRequest.toEntity());

        return toResponse(book);
    }

    @Transactional(readOnly = true)
    @Override
    public BookResponse findByBookId(long bookId) {

        Book book = jpaBookRepository.findById(bookId).orElse(null);
        if(Objects.isNull(book)) {
            throw new BookNotFoundException();
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

        if(Objects.isNull(updateBookRequest)) {
            throw new IllegalArgumentException("updateBookRequest cannot be null");
        }

        if(!jpaBookRepository.existsById(updateBookRequest.bookId())) {
            throw new IllegalArgumentException("Book does not exist");
        }

        return toResponse(jpaBookRepository.save(updateBookRequest.toEntity()));
    }

    @Transactional
    @Override
    public void deleteByBookId(long bookId) {

        if(!jpaBookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("Book does not exist");
        }

        jpaBookRepository.deleteById(bookId);
    }

}
