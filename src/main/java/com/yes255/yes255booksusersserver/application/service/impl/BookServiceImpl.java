package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.*;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.BookNotFoundException;
import com.yes255.yes255booksusersserver.common.exception.QuantityInsufficientException;
import com.yes255.yes255booksusersserver.common.exception.ValidationFailedException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.BookAuthor;
import com.yes255.yes255booksusersserver.persistance.domain.BookCategory;
import com.yes255.yes255booksusersserver.persistance.domain.BookTag;
import com.yes255.yes255booksusersserver.persistance.domain.Category;
import com.yes255.yes255booksusersserver.persistance.domain.Likes;
import com.yes255.yes255booksusersserver.persistance.domain.Review;
import com.yes255.yes255booksusersserver.persistance.domain.enumtype.OperationType;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookAuthorRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookCategoryRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaBookTagRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCategoryRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaLikesRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final JpaBookRepository jpaBookRepository;
    private final JpaCategoryRepository jpaCategoryRepository;
    private final JpaBookCategoryRepository jpaBookCategoryRepository;
    private final JpaBookTagRepository jpaBookTagRepository;
    private final JpaBookAuthorRepository jpaBookAuthorRepository;
    private final JpaLikesRepository jpaLikesRepository;
    private final BookCategoryService bookCategoryService;
    private final BookTagService bookTagService;
    private final BookAuthorService bookAuthorService;
    private final AuthorService authorService;


    @Transactional
    @Override
    public BookResponse createBook(CreateBookRequest request, List<Long> categoryIdList, List<Long> tagIdList) {

        if(Objects.isNull(request)) {
            throw new ApplicationException(
                    ErrorStatus.toErrorStatus("요청 값이 비어있습니다.", 400, LocalDateTime.now())
            );
        }

        BookResponse bookResponse = getBookByIsbn(request.bookIsbn());

        if(bookResponse != null) {
            //삭제된 책일시 삭제 상태 업데이트 및 전체 수정
            if(bookResponse.bookIsDeleted()) {

                updateBookIsDeleteFalse(bookResponse.bookId());

                UpdateBookRequest updateBookRequest = UpdateBookRequest.fromCreateBookRequest(request, bookResponse.bookId());

                List<BookCategoryResponse> bookCategoryList = bookCategoryService.getBookCategoryByBookId(updateBookRequest.bookId());
                List<BookTagResponse> bookTagList = bookTagService.getBookTagByBookId(updateBookRequest.bookId());
                List<BookAuthorResponse> bookAuthorList = bookAuthorService.getBookAuthorByBookId(updateBookRequest.bookId());

                for(BookCategoryResponse bookCategory : bookCategoryList) {
                    bookCategoryService.removeBookCategory(bookCategory.bookCategoryId());
                }

                for(BookTagResponse bookTag : bookTagList) {
                    bookTagService.removeBookTag(bookTag.bookTagId());
                }

                for(BookAuthorResponse bookAuthor : bookAuthorList) {
                    bookAuthorService.removeBookAuthor(bookAuthor.bookAuthorId());
                }

                BookResponse response = updateBook(updateBookRequest);
                categoryIdList.forEach(categoryId -> bookCategoryService.createBookCategory(response.bookId(), categoryId));

                if(tagIdList != null) {
                    for(Long tagId : tagIdList) {
                        bookTagService.createBookTag(new CreateBookTagRequest(response.bookId(), tagId));
                    }
                }

                List<String> authorStringList = Arrays.stream(request.bookAuthor().split(","))
                        .map(String::trim)
                        .distinct()
                        .toList();

                for(String authorString : authorStringList) {

                    if(authorService.isExistAuthorByName(authorString)) {
                        bookAuthorService.createBookAuthor(new CreateBookAuthorRequest(response.bookId(), authorService.getAuthorByName(authorString).authorId()));
                    } else {
                        AuthorResponse createAuthorResponse = authorService.createAuthor(new CreateAuthorRequest(authorString));
                        bookAuthorService.createBookAuthor(new CreateBookAuthorRequest(response.bookId(), createAuthorResponse.authorId()));
                    }
                }

                return response;

            } else {
                throw new ApplicationException(ErrorStatus.toErrorStatus("이미 존재하는 책입니다.", 400, LocalDateTime.now()));
            }

        }

        List<String> authorStringList = Arrays.stream(request.bookAuthor().split(","))
                .map(String::trim)
                .distinct()
                .toList();

        Book book = jpaBookRepository.save(request.toEntity());

        for(String authorString : authorStringList) {

            if(authorService.isExistAuthorByName(authorString)) {
                bookAuthorService.createBookAuthor(new CreateBookAuthorRequest(book.getBookId(), authorService.getAuthorByName(authorString).authorId()));
            } else {
                AuthorResponse createAuthorResponse = authorService.createAuthor(new CreateAuthorRequest(authorString));
                bookAuthorService.createBookAuthor(new CreateBookAuthorRequest(book.getBookId(), createAuthorResponse.authorId()));
            }
        }

        for(Long categoryId : categoryIdList) {
            bookCategoryService.createBookCategory(book.getBookId(), categoryId);
        }

        if(tagIdList != null) {
            for(Long tagId : tagIdList) {
                bookTagService.createBookTag(new CreateBookTagRequest(book.getBookId(), tagId));
            }
        }

        return toResponse(book);
    }

    @Transactional(readOnly = true)
    @Override
    public BookResponse getBook(long bookId) {

        Book book = jpaBookRepository.findById(bookId).orElseThrow(() -> new ApplicationException(ErrorStatus.toErrorStatus("요청 값이 비어있습니다.", 400, LocalDateTime.now())));

        if(Objects.isNull(book) || book.isBookIsDeleted()) {
            throw new BookNotFoundException(
                    ErrorStatus.toErrorStatus("알맞은 책을 찾을 수 없습니다.", 404, LocalDateTime.now())
            );
        }

        return toResponse(book);
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    @Override
    public List<BookResponse> getAllBooks() {
        return jpaBookRepository.findByBookIsDeletedFalse().stream().map(this::toResponse).toList();
    }

    @Transactional
    @Override
    public BookResponse updateBook(UpdateBookRequest request, List<Long> categoryIdList, List<Long> tagIdList) {

        List<BookCategoryResponse> bookCategoryList = bookCategoryService.getBookCategoryByBookId(request.bookId());
        List<BookTagResponse> bookTagList = bookTagService.getBookTagByBookId(request.bookId());
        List<BookAuthorResponse> bookAuthorList = bookAuthorService.getBookAuthorByBookId(request.bookId());

        for(BookCategoryResponse bookCategory : bookCategoryList) {
            bookCategoryService.removeBookCategory(bookCategory.bookCategoryId());
        }

        for(BookTagResponse bookTag : bookTagList) {
            bookTagService.removeBookTag(bookTag.bookTagId());
        }

        for(BookAuthorResponse bookAuthor : bookAuthorList) {
            bookAuthorService.removeBookAuthor(bookAuthor.bookAuthorId());
        }

        Book existingBook = jpaBookRepository.findById(request.bookId())
                .orElseThrow(() -> new BookNotFoundException(
                ErrorStatus.toErrorStatus("알맞은 책을 찾을 수 없습니다.", 404, LocalDateTime.now())
                ));

        existingBook.updateAll(request.toEntity());

        BookResponse response = toResponse(existingBook);

        categoryIdList.forEach(categoryId -> bookCategoryService.createBookCategory(response.bookId(), categoryId));

        if(tagIdList != null) {
            for(Long tagId : tagIdList) {
                bookTagService.createBookTag(new CreateBookTagRequest(response.bookId(), tagId));
            }
        }

        List<String> authorStringList = Arrays.stream(request.bookAuthor().split(","))
                .map(String::trim)
                .distinct()
                .toList();

        for(String authorString : authorStringList) {

            if(authorService.isExistAuthorByName(authorString)) {
                bookAuthorService.createBookAuthor(new CreateBookAuthorRequest(response.bookId(), authorService.getAuthorByName(authorString).authorId()));
            } else {
                AuthorResponse createAuthorResponse = authorService.createAuthor(new CreateAuthorRequest(authorString));
                bookAuthorService.createBookAuthor(new CreateBookAuthorRequest(response.bookId(), createAuthorResponse.authorId()));
            }
        }

        return response;
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
        List<BookAuthor> bookAuthorList = jpaBookAuthorRepository.findByBook(book);
        List<Likes> likesList = jpaLikesRepository.findByBook(book);

        jpaBookCategoryRepository.deleteAll(bookCategoryList);
        jpaBookTagRepository.deleteAll(bookTagList);
        jpaBookAuthorRepository.deleteAll(bookAuthorList);
        jpaLikesRepository.deleteAll(likesList);
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

        Comparator<BookResponse> comparator = getComparator(sortString);

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

    @Transactional(readOnly = true)
    @Override
    public BookResponse getBookByIsbn(String isbn) {
        return jpaBookRepository.findByBookIsbn(isbn).map(this::toResponse).orElse(null);
    }

    @Transactional
    @Override
    public void updateBookIsDeleteFalse(Long bookId) {

        Book book = jpaBookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(
                ErrorStatus.toErrorStatus("삭제 상태를 업데이트 할 책을 찾지 못했습니다.", 404, LocalDateTime.now())
        ));

        book.updateBookIsDeleted(false);

    }

    @Transactional
    @Override
    public List<BookResponse> updateQuantity(UpdateBookQuantityRequest request) {

        if(request.bookIdList().size() != request.quantityList().size()) {
            throw new ApplicationException(ErrorStatus.toErrorStatus("책 리스트와 수량 리스트의 개수가 다릅니다.", 400, LocalDateTime.now()));
        }

        List<BookResponse> updatedBookList = new ArrayList<>();

        for(int i = 0; i< request.bookIdList().size(); i++) {

            BookResponse book = getBook(request.bookIdList().get(i));

            Integer updatedQuantity;

            if(request.operationType() == OperationType.DECREASE) {
                if(request.quantityList().get(i) > book.bookQuantity()) {
                    throw new QuantityInsufficientException(ErrorStatus.toErrorStatus("주문 한 수량이 재고보다 많습니다.", 400, LocalDateTime.now()));
                }
                updatedQuantity = book.bookQuantity() - request.quantityList().get(i);
            } else {
                updatedQuantity = request.quantityList().get(i) + book.bookQuantity();
            }

            UpdateBookRequest updatedBook = UpdateBookRequest.builder()
                    .bookId(book.bookId())
                    .bookIsbn(book.bookIsbn())
                    .bookName(book.bookName())
                    .bookDescription(book.bookDescription())
                    .bookPublisher(book.bookPublisher())
                    .bookPublishDate(book.bookPublishDate())
                    .bookPrice(book.bookPrice())
                    .bookSellingPrice(book.bookSellingPrice())
                    .imageURL(book.bookImage())
                    .quantity(updatedQuantity)
                    .bookIsPackable(book.bookIsPackable())
                    .build();

            Book existingBook = jpaBookRepository.findById(updatedBook.bookId())
                    .orElseThrow(() -> new BookNotFoundException(
                            ErrorStatus.toErrorStatus("알맞은 책을 찾을 수 없습니다.", 404, LocalDateTime.now())
                    ));

            existingBook.updateAll(updatedBook.toEntity());

            updatedBookList.add(toResponse(existingBook));
        }

        return updatedBookList;
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
                .bookIsDeleted(book.isBookIsDeleted())
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

    private Comparator<BookResponse> getComparator(String sortString) {

        return switch (sortString) {
            case "low-price" -> Comparator.comparing(BookResponse::bookSellingPrice);
            case "high-price" -> Comparator.comparing(BookResponse::bookSellingPrice).reversed();
            case "grade" -> Comparator.comparing(BookResponse::grade);
            case "review" -> Comparator.comparingInt(BookResponse::reviewCount).reversed();
            case "new-product" -> Comparator.comparing(BookResponse::bookPublishDate).reversed();
            default -> Comparator.comparingInt(BookResponse::hitsCount).reversed();
        };
    }
}
