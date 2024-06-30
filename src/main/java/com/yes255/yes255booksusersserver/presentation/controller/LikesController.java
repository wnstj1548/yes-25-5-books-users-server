package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.LikesService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.common.jwt.annotation.CurrentUser;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateLikesRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateLikesRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.LikesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자의 좋아요 관련 작업을 처리하는 RestController
 */
@Tag(name = "좋아요 API", description = "좋아요 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/books/likes")
public class LikesController {
    private final LikesService likesService;

    /**
     * 특정 사용자가 좋아요한 항목들을 조회합니다.
     *
     * @param  jwtUserDetails 사용자의 JWT Token
     * @return ResponseEntity<List<LikesResponse>> 형식의 좋아요 목록
     */
    @Operation(summary = "사용자의 좋아요 목록 조회", description = "사용자의 좋아요 목록을 조회합니다.")
    @GetMapping("/users")
    public ResponseEntity<List<LikesResponse>> findByUserId(@CurrentUser JwtUserDetails jwtUserDetails) {

        Long userId = jwtUserDetails.userId();

        return ResponseEntity.ok(likesService.getLikeByUserId(userId));
    }

    /**
     * 특정 책에 대한 좋아요 정보를 조회합니다.
     *
     * @param bookId 조회할 책의 ID
     * @return ResponseEntity<List<LikesResponse>> 형식의 책에 대한 좋아요 목록
     */
    @Operation(summary = "특정 책의 좋아요 조회", description = "특정 책의 좋아요 목록을 조회합니다..")
    @GetMapping("/books/{bookId}")
    public ResponseEntity<List<LikesResponse>> findByBookId(@PathVariable Long bookId) {
        return ResponseEntity.ok(likesService.getLikeByBookId(bookId));
    }

    /**
     * 좋아요 상태를 업데이트합니다.
     *
     * @param bookId 업데이트할 책 정보를 담은 bookId
     * @param jwtUserDetails 업데이트할 유저 정보를 담은 jwt Token
     * @return ResponseEntity<LikesResponse> 형식의 업데이트된 좋아요 정보
     */
    @Operation(summary = "좋아요 상태 업데이트", description = "좋아요 상태를 업데이트합니다.")
    @Parameter(name = "request", description = "bookId(도서 PK), userId(유저 PK) 를 포함합니다.")
    @PutMapping
    public ResponseEntity<LikesResponse> update(Long bookId, @CurrentUser JwtUserDetails jwtUserDetails) {
        return ResponseEntity.ok(likesService.updateLikeStatus(bookId, jwtUserDetails.userId()));
    }

    /**
     * 새로운 좋아요를 생성합니다.
     *
     * @param request 생성할 좋아요 정보를 담은 CreateLikesRequest 객체
     * @return ResponseEntity<LikesResponse> 형식의 생성된 좋아요 정보
     */
    @Operation(summary = "새로운 좋아요 생성", description = "새로운 좋아요를 생성합니다.")
    @Parameter(name = "request", description = "bookId(도서 PK), userId(유저 PK) 를 포함합니다.")
    @PostMapping
    public ResponseEntity<LikesResponse> create(@RequestBody CreateLikesRequest request) {
        return ResponseEntity.ok(likesService.createLike(request));
    }

}
