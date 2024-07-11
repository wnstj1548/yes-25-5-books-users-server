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
            bookOrderResponseList.add(toBookOrderResponse(book));
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

        Comparator comparator = getComparator(sortString);

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

    @Transactional(readOnly = true)
    @Override
    public List<BookCouponResponse> getBookByName(String name) {
        return jpaBookRepository.findByBookNameContainingIgnoreCaseAndBookIsDeletedFalse(name)
                .stream().map(this::toBookCouponResponse).toList();
    }

    @Transactional
    @Override
    public void addHitsCount(Long bookId) {

        Book book = jpaBookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(ErrorStatus.toErrorStatus("해당하는 책이 없습니다.", 404, LocalDateTime.now())));

        book.updateBookHitsCount(book.getHitsCount() + 1);

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
                .reviewCount(book.getReviews().size())
                .hitsCount(book.getHitsCount())
                .searchCount(book.getSearchCount())
                .bookIsPackable(book.isBookIsPackable())
                .grade(book.getReviews().stream().mapToDouble(Review::getRating).average().orElse(0))
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

    public BookOrderResponse toBookOrderResponse(Book book) {

        List<BookAuthor> bookAuthorList = jpaBookAuthorRepository.findByBook(book);

        String authorString = bookAuthorList.stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getAuthorName())
                .collect(Collectors.joining(","));

        return BookOrderResponse.builder()
                .bookId(book.getBookId())
                .bookName(book.getBookName())
                .bookPrice(book.getBookPrice())
                .bookIsPackable(book.isBookIsPackable())
                .bookImage(book.getBookImage())
                .quantity(book.getQuantity())
                .author(authorString)
                .build();
    }

    private Comparator getComparator(String sortString) {

        Comparator comparator;

        switch(sortString) {
            case "low-price":
                comparator = Comparator.comparing(BookResponse::bookSellingPrice);
                break;
            case "high-price":
                comparator = Comparator.comparing(BookResponse::bookSellingPrice).reversed();
                break;
            case "grade" :
                comparator = Comparator.comparing(BookResponse::grade);
                break;
            case "review":
                comparator = Comparator.comparingInt(BookResponse::reviewCount).reversed();
                break;
            case "popularity":
            default:
                comparator = Comparator.comparingInt(BookResponse::hitsCount).reversed();
                break;
        }

        return comparator;
    }
}
