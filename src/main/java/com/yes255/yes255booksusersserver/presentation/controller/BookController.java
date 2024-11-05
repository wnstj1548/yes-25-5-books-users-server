
package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.*;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.QuantityInsufficientException;
import com.yes255.yes255booksusersserver.common.exception.ValidationFailedException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.persistance.domain.enumtype.OperationType;
import com.yes255.yes255booksusersserver.presentation.dto.request.*;
import com.yes255.yes255booksusersserver.presentation.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 도서에 관련된 API 처리를 하는 RestController
 */
@Tag(name = "도서 API", description = "도서 관리 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    /**
     * 모든 책을 가져옵니다.
     *
     * @return ResponseEntity<Page<BookResponse>> 형식의 모든 책 정보.
     */
    @Operation(summary = "모든 책 페이지 조회", description = "등록된 모든 책을 페이징 처리하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 책 페이지 조회 성공", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/books/page")
    public ResponseEntity<Page<BookResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(bookService.getAllBooks(pageable));
    }

    /**
     * 모든 책을 가져옵니다.
     *
     * @return ResponseEntity<List<BookResponse>> 형식의 모든 책 정보.
     */
    @Operation(summary = "모든 책 리스트 조회", description = "등록된 모든 책을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 책 리스트 조회 성공", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "책 아이디로 특정 책 조회", content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/books/{bookId}")
    public ResponseEntity<BookResponse> findById(@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(bookService.getBook(bookId));
    }

    @Operation(summary = "아이디 리스트로 책 조회" ,description = "bookId를 List로 받아 해당 도서의 이름을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아이디 리스트로 책 조회", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/books/orders")
    public ResponseEntity<List<BookOrderResponse>> findByOrder(@RequestParam List<Long> bookIdList) {
        return ResponseEntity.ok(bookService.getBooksByOrder(bookIdList));
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "새로운 책 생성", content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PostMapping("/books")
    public ResponseEntity<BookResponse> create(@RequestBody @Valid CreateBookRequest request, @RequestParam(value = "categoryIdList") List<Long> categoryIdList, @RequestParam(value = "tagIdList", required = false) List<Long> tagIdList, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        return ResponseEntity.ok(bookService.createBook(request, categoryIdList, tagIdList));
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "책 업데이트 성공", content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PutMapping("/books")
    public ResponseEntity<BookResponse> update(@RequestBody @Valid UpdateBookRequest request, @RequestParam(value = "categoryIdList") List<Long> categoryIdList, @RequestParam(value = "tagIdList", required = false) List<Long> tagIdList, BindingResult bindingResult ) {

        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }

        return ResponseEntity.ok(bookService.updateBook(request, categoryIdList, tagIdList));
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "책 리스트 수량 수정", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PatchMapping("/books")
    public ResponseEntity<List<BookResponse>> updateQuantity(@RequestBody UpdateBookQuantityRequest request) {

        if(request.bookIdList().size() != request.quantityList().size()) {
            throw new ApplicationException(ErrorStatus.toErrorStatus("책 리스트와 수량 리스트의 개수가 다릅니다.", 400, LocalDateTime.now()));
        }

        return ResponseEntity.ok(bookService.updateQuantity(request));
    }

    /**
     * 책을 삭제합니다.
     *
     * @param bookId 삭제할 책의 ID
     * @return 삭제 성공 여부를 나타내는 ResponseEntity
     */
    @Operation(summary = "특정 책 삭제", description = "bookId로 책을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 책 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Void> delete(@PathVariable Long bookId) {

        bookService.removeBook(bookId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "책 이름 검색", description = "책 이름을 받아 그 단어가 포함된 모든 도서를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "책 이름으로 검색 성공", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/books/search")
    public ResponseEntity<List<BookCouponResponse>> searchByName(@RequestParam String query) {
        return ResponseEntity.ok(bookService.getBookByName(query));
    }

    @Operation(summary = "카테고리로 책 조회", description = "카테고리 아이디로 해당 카테고리인 책들을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리로 책 조회 성공", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/books/category/{categoryId}/page")
    public ResponseEntity<Page<BookResponse>> getBookByCategory(@PathVariable Long categoryId, Pageable pageable, @RequestParam(defaultValue = "popularity") String sortString) {
        return ResponseEntity.ok(bookService.getBookByCategoryId(categoryId, pageable, sortString));
    }

    @Operation(summary = "책 조회수 1회 추가", description = "책 조회수를 1회 추가합니다..")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "책 조회수 1회 추가 성공", content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/books/{bookId}/addHitsCount")
    public ResponseEntity<Void> addHitsCount(@PathVariable Long bookId) {

        bookService.addHitsCount(bookId);

        return ResponseEntity.noContent().build();
    }
}
