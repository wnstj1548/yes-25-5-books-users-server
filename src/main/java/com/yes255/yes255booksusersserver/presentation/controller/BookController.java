
package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.BookCategoryService;
import com.yes255.yes255booksusersserver.application.service.BookService;
import com.yes255.yes255booksusersserver.application.service.BookTagService;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.QuantityInsufficientException;
import com.yes255.yes255booksusersserver.common.exception.ValidationFailedException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.enumtype.OperationType;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateBookTagRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookQuantityRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateBookRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookCategoryResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.BookTagResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 도서에 관련된 API 처리를 하는 RestController
 */
@Tag(name = "도서 API", description = "도서 관리 API")
@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookCategoryService bookCategoryService;
    private final BookTagService bookTagService;

    /**
     * 모든 책을 가져옵니다.
     *
     * @return ResponseEntity<Page<BookResponse>> 형식의 모든 책 정보.
     */
    @Operation(summary = "모든 책 조회", description = "등록된 모든 책을 페이징 처리하여 조회합니다.")
    @GetMapping("/books/page")
    public ResponseEntity<Page<BookResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    /**
     * 모든 책을 가져옵니다.
     *
     * @return ResponseEntity<List<BookResponse>> 형식의 모든 책 정보.
     */
    @Operation(summary = "모든 책 조회", description = "등록된 모든 책을 조회합니다.")
    @GetMapping("/books")
    public ResponseEntity<List<BookResponse>> findAll() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /**
     * 책 ID로 특정 책을 조회합니다.
     *
     * @param bookId 조회할 책의 ID
     * @return ResponseEntity<BookResponse> 형식의 특정 책 정보
     */
    @Operation(summary = "특정 책 조회", description = "등록된 책 중 bookId(PK)를 통해 특정 책을 조회합니다.")
    @GetMapping("/books/{bookId}")
    public ResponseEntity<BookResponse> findById(@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(bookService.getBook(bookId));
    }

    /**
     * 새로운 책을 생성합니다.
     *
     * @param request       생성할 책의 정보를 담은 CreateBookRequest 객체
     * @param categoryIdList 책과 연결될 카테고리 ID 목록
     * @param tagIdList     선택적으로 책과 연결될 태그 ID 목록
     * @param bindingResult 유효성 검사 결과를 담은 BindingResult 객체
     * @return ResponseEntity<BookResponse> 형식의 생성된 책 정보
     * @throws ValidationFailedException 요청에 유효성 검사 오류가 있는 경우 발생합니다.
     */
    @Operation(summary = "새로운 책 생성", description = "새로운 책을 생성합니다.")
    @Parameter(name = "request", description = "ISBN(책 고유번호), name(제목), price(책 가격), sellingPrice(판매 가격), quantity(수량) 를 포함합니다.")
    @PostMapping("/books")
    public ResponseEntity<BookResponse> create(@RequestBody @Valid CreateBookRequest request, @RequestParam(value = "categoryIdList") List<Long> categoryIdList, @RequestParam(value = "tagIdList", required = false) List<Long> tagIdList, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        BookResponse response = bookService.createBook(request);

        for(Long categoryId : categoryIdList) {
            bookCategoryService.createBookCategory(response.bookId(), categoryId);
        }

        if(tagIdList != null) {
            for(Long tagId : tagIdList) {
                bookTagService.createBookTag(new CreateBookTagRequest(response.bookId(), tagId));
            }
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 기존 책을 업데이트합니다.
     *
     * @param request       업데이트할 책의 정보를 담은 UpdateBookRequest 객체
     * @param categoryIdList 업데이트된 책과 연결될 카테고리 ID 목록
     * @param tagIdList     선택적으로 업데이트된 책과 연결될 태그 ID 목록
     * @param bindingResult 유효성 검사 결과를 담은 BindingResult 객체
     * @return ResponseEntity<BookResponse> 형식의 업데이트된 책 정보
     * @throws ValidationFailedException 요청에 유효성 검사 오류가 있는 경우 발생합니다.
     */
    @Operation(summary = "책 업데이트", description = "기존 책을 업데이트합니다.")
    @Parameter(name = "request", description = "bookId(PK) ,ISBN(책 고유번호), name(제목), price(책 가격), sellingPrice(판매 가격), quantity(수량) 를 포함합니다.")
    @PutMapping("/books")
    public ResponseEntity<BookResponse> update(@RequestBody @Valid UpdateBookRequest request, @RequestParam(value = "categoryIdList") List<Long> categoryIdList, @RequestParam(value = "tagIdList", required = false) List<Long> tagIdList, BindingResult bindingResult ) {

        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        List<BookCategoryResponse> bookCategoryList = bookCategoryService.getBookCategoryByBookId(request.bookId());
        List<BookTagResponse> bookTagList = bookTagService.getBookTagByBookId(request.bookId());

        for(BookCategoryResponse bookCategory : bookCategoryList) {
            bookCategoryService.removeBookCategory(bookCategory.bookCategoryId());
        }

        for(BookTagResponse bookTag : bookTagList) {
            bookTagService.removeBookTag(bookTag.bookTagId());
        }

        BookResponse response = bookService.updateBook(request);
        categoryIdList.forEach(categoryId -> bookCategoryService.createBookCategory(response.bookId(), categoryId));

        if(tagIdList != null) {
            for(Long tagId : tagIdList) {
                bookTagService.createBookTag(new CreateBookTagRequest(response.bookId(), tagId));
            }
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 책의 수량을 업데이트합니다.
     *
     * @param request 업데이트 할 책의 id 리스트와 수량 리스트가 포함되어 있습니다.
     * @return ResponseEntity<List<BookResponse>> 형식의 업데이트된 책 정보
     * @throws QuantityInsufficientException 요청 수량이 재고보다 많은 경우 발생합니다.
     */
    @Operation(summary = "책 리스트 수량 수정", description = "책의 아이디 리스트와 수량 리스트를 받아 현재 책의 재고를 업데이트합니다.")
    @Parameter(name = "request", description = " bookIdList(구매하는 책 id 리스트), quantityList (구매하는 책의 수량 리스트) 를 포함합니다.")
    @PatchMapping("/books")
    public ResponseEntity<List<BookResponse>> updateQuantity(@RequestBody UpdateBookQuantityRequest request) {

        if(request.bookIdList().size() != request.quantityList().size()) {
            throw new ApplicationException(ErrorStatus.toErrorStatus("책 리스트와 수량 리스트의 개수가 다릅니다.", 400, LocalDateTime.now()));
        }

        List<BookResponse> updatedBookList = new ArrayList<>();

        for(int i = 0; i< request.bookIdList().size(); i++) {

            BookResponse book = bookService.getBook(request.bookIdList().get(i));

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
                    .bookAuthor(book.bookAuthor())
                    .bookPublisher(book.bookPublisher())
                    .bookPublishDate(book.bookPublishDate())
                    .bookPrice(book.bookPrice())
                    .bookSellingPrice(book.bookSellingPrice())
                    .bookImage(book.bookImage())
                    .quantity(updatedQuantity)
                    .build();

            updatedBookList.add(bookService.updateBook(updatedBook));
        }

        return ResponseEntity.ok(updatedBookList);
    }

    /**
     * 책을 삭제합니다.
     *
     * @param bookId 삭제할 책의 ID
     * @return 삭제 성공 여부를 나타내는 ResponseEntity
     */
    @Operation(summary = "특정 책 삭제", description = "bookId로 책을 삭제합니다.")
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Void> delete(@PathVariable Long bookId) {

        bookService.removeBook(bookId);

        return ResponseEntity.noContent().build();
    }
}
