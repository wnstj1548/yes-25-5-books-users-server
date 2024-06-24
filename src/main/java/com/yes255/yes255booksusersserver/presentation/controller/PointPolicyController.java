package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.PointPolicyService;
import com.yes255.yes255booksusersserver.presentation.dto.request.pointpolicy.PointPolicyRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.pointpolicy.PointPolicyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class PointPolicyController {

    private final PointPolicyService pointPolicyService;

    // 포인트 정책 생성
    @PostMapping("/point-policies")
    public ResponseEntity<PointPolicyResponse> createPointPolicy(@RequestBody PointPolicyRequest policyRequest) {
        return new ResponseEntity<>(pointPolicyService.createPointPolicy(policyRequest), HttpStatus.CREATED);
    }

    // 포인트 정책 단건 조회
    @GetMapping("/point-policies/{pointPolicyId}")
    public ResponseEntity<PointPolicyResponse> findPointPolicyById(@PathVariable Long pointPolicyId) {
        return new ResponseEntity<>(pointPolicyService.findPointPolicyById(pointPolicyId), HttpStatus.OK);
    }

    // 포인트 정책 목록 조회
    @GetMapping("/point-policies")
    public ResponseEntity<List<PointPolicyResponse>> findAllPointPolicies() {
        return new ResponseEntity<>(pointPolicyService.findAllPointPolicies(), HttpStatus.OK);
    }

    // 포인트 정책 수정
    @PutMapping("/point-policies/{pointPolicyId}")
    public ResponseEntity<PointPolicyResponse> updatePointPolicy(@PathVariable Long pointPolicyId,
                                                                 @RequestBody PointPolicyRequest policyRequest) {
        return new ResponseEntity<>(pointPolicyService.updatePointPolicyById(pointPolicyId, policyRequest), HttpStatus.OK);
    }

    // 포인트 정책 삭제
    @DeleteMapping("/point-policies/{pointPolicyId}")
    public ResponseEntity<Void> deletePointPolicy(@PathVariable Long pointPolicyId) {

        pointPolicyService.deletePointPolicyById(pointPolicyId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
