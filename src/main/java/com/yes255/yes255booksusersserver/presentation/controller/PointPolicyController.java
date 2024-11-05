package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.PointPolicyService;
import com.yes255.yes255booksusersserver.presentation.dto.request.pointpolicy.CreatePointPolicyRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.pointpolicy.PointPolicyRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.pointpolicy.PointPolicyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 포인트 정책 관련 API를 제공하는 PointPolicyController
 */

@Tag(name = "포인트 정책 API", description = "포인트 정책 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/admin")
public class PointPolicyController {

    private final PointPolicyService pointPolicyService;

    /**
     * 포인트 정책을 생성합니다.
     *
     * @param policyRequest 포인트 정책 생성 요청 데이터
     * @return 생성된 포인트 정책 응답 데이터와 상태 코드 201(CREATED)
     */
    @Operation(summary = "포인트 정책 생성", description = "포인트 정책을 생성합니다.")
    @PostMapping("/point-policies")
    public ResponseEntity<PointPolicyResponse> createPointPolicy(@RequestBody CreatePointPolicyRequest policyRequest) {
        return new ResponseEntity<>(pointPolicyService.createPointPolicy(policyRequest), HttpStatus.CREATED);
    }

    /**
     * 특정 포인트 정책을 조회합니다.
     *
     * @param pointPolicyId 조회할 포인트 정책 ID
     * @return 조회된 포인트 정책 응답 데이터와 상태 코드 200(OK)
     */
    @Operation(summary = "포인트 정책 조회", description = "특정 포인트 정책을 조회합니다.")
    @GetMapping("/point-policies/{pointPolicyId}")
    public ResponseEntity<PointPolicyResponse> findPointPolicyById(@PathVariable Long pointPolicyId) {
        return new ResponseEntity<>(pointPolicyService.findPointPolicyById(pointPolicyId), HttpStatus.OK);
    }

    /**
     * 모든 포인트 정책 목록을 조회합니다.
     *
     * @return 포인트 정책 목록 응답 데이터와 상태 코드 200(OK)
     */
    @Operation(summary = "포인트 정책 목록 조회", description = "모든 포인트 정책 목록을 조회합니다.")
    @GetMapping("/point-policies")
    public ResponseEntity<Page<PointPolicyResponse>> findAllPointPolicies(Pageable pageable) {
        return new ResponseEntity<>(pointPolicyService.findAllPointPolicies(pageable), HttpStatus.OK);
    }

    /**
     * 특정 포인트 정책을 수정합니다.
     *
     * @param pointPolicyId 수정할 포인트 정책 ID
     * @param policyRequest 포인트 정책 수정 요청 데이터
     * @return 수정된 포인트 정책 응답 데이터와 상태 코드 200(OK)
     */
    @Operation(summary = "포인트 정책 수정", description = "특정 포인트 정책을 수정합니다.")
    @PutMapping("/point-policies/{pointPolicyId}")
    public ResponseEntity<PointPolicyResponse> updatePointPolicy(@PathVariable Long pointPolicyId,
                                                                 @RequestBody PointPolicyRequest policyRequest) {
        return new ResponseEntity<>(pointPolicyService.updatePointPolicyById(pointPolicyId, policyRequest), HttpStatus.OK);
    }

    /**
     * 특정 포인트 정책을 삭제합니다.
     *
     * @param pointPolicyId 삭제할 포인트 정책 ID
     * @return 상태 코드 200(OK)
     */
    @Operation(summary = "포인트 정책 삭제", description = "특정 포인트 정책을 삭제합니다.")
    @DeleteMapping("/point-policies/{pointPolicyId}")
    public ResponseEntity<Void> deletePointPolicy(@PathVariable Long pointPolicyId) {

        pointPolicyService.deletePointPolicyById(pointPolicyId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
