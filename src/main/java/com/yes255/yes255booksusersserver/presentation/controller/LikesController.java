package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.LikesService;
import com.yes255.yes255booksusersserver.common.exception.ApplicationException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.common.jwt.annotation.CurrentUser;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateLikesRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateLikesRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.LikesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 좋아요 목록 조회 성공", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/users")
    public ResponseEntity<List<LikesResponse>> findByUserId(@CurrentUser JwtUserDetails jwtUserDetails) {

        if(jwtUserDetails == null) {
            throw new ApplicationException(ErrorStatus.toErrorStatus("로그인한 회원만 좋아요를 할 수 있습니다.", 400, LocalDateTime.now()));
        }

        return ResponseEntity.ok(likesService.getLikeByUserId(jwtUserDetails.userId()));
    }

    /**
     * 특정 책에 대한 좋아요 정보를 조회합니다.
     *
     * @param bookId 조회할 책의 ID
     * @return ResponseEntity<List<LikesResponse>> 형식의 책에 대한 좋아요 목록
     */
    @Operation(summary = "특정 책의 좋아요 조회", description = "특정 책의 좋아요 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "책으로 좋아요 목록 조회 성공", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
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
    @Operation(summary = "좋아요 상태 생성 및 업데이트", description = "좋아요를 생성하거나 존재하면 업데이트합니다.")
    @Parameter(name = "request", description = "bookId(도서 PK), userId(유저 PK) 를 포함합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 상태 생성 및 업데이트 성공", content = @Content(schema = @Schema(implementation = LikesResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @PostMapping("{bookId}")
    public ResponseEntity<LikesResponse> click(@PathVariable Long bookId, @CurrentUser JwtUserDetails jwtUserDetails) {

        if(Objects.isNull(jwtUserDetails)) {
            throw new ApplicationException(ErrorStatus.toErrorStatus("로그인한 회원만 좋아요를 할 수 있습니다.", 400, LocalDateTime.now()));
        }

        if(!likesService.isExistByBookIdAndUserId(bookId, jwtUserDetails.userId())) {
            return ResponseEntity.ok(likesService.createLike(bookId, jwtUserDetails.userId()));
        }

        return ResponseEntity.ok(likesService.updateLikeStatus(bookId, jwtUserDetails.userId()));
    }

    @Operation(summary = "좋아요 검색", description = "책의 아이디와 토큰에 들어있는 유저로 좋아요를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 조회 성공", content = @Content(schema = @Schema(implementation = LikesResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/{bookId}")
    public ResponseEntity<LikesResponse> findByBookIdAndUserId(@PathVariable Long bookId, @CurrentUser JwtUserDetails jwtUserDetails) {

        if(Objects.isNull(jwtUserDetails)) {
            throw new ApplicationException(ErrorStatus.toErrorStatus("로그인한 회원만 좋아요를 할 수 있습니다.", 400, LocalDateTime.now()));
        }

        if(likesService.isExistByBookIdAndUserId(bookId, jwtUserDetails.userId())) {
            return ResponseEntity.ok(likesService.getLikeByBookIdAndUserId(bookId, jwtUserDetails.userId()));
        }

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "좋아요 존재 확인", description = "책의 아이디와 토큰에 들어있는 유저로 좋아요가 존재하는지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 존재 확인 성공", content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/{bookId}/exist")
    public ResponseEntity<Boolean> exist(@PathVariable Long bookId, @CurrentUser JwtUserDetails jwtUserDetails) {

        if(jwtUserDetails == null) {
            return ResponseEntity.ok(false);
        }

        return ResponseEntity.ok(likesService.isExistByBookIdAndUserId(bookId, jwtUserDetails.userId()));
    }
}
