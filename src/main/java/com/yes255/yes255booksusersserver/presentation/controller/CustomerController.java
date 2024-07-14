package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.CustomerService;
import com.yes255.yes255booksusersserver.presentation.dto.request.customer.CustomerRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.customer.CustomerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 고객 관련 API를 제공하는 CustomerController
 */

@Tag(name = "고객 API", description = "고객 관련 API 입니다.")
@RequestMapping("users/customers")
@RequiredArgsConstructor
@RestController
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 새로운 고객을 생성합니다.
     *
     * @param customerRequest 생성할 고객 정보
     * @return 생성된 고객 정보와 상태 코드 200(OK)
     */
    @Operation(summary = "고객 정보 생성", description = "새로운 고객을 생성합니다.")
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest customerRequest) {
        return ResponseEntity.ok(customerService.createCustomer(customerRequest));
    }

    /**
     * 특정 고객 정보를 수정합니다.
     *
     * @param customerId 수정할 고객의 ID
     * @param customerRequest 수정된 고객 정보
     * @return 수정된 고객 정보와 상태 코드 200(OK)
     */
    @Operation(summary = "고객 정보 수정", description = "특정 고객 정보를 수정합니다.")
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long customerId, @RequestBody CustomerRequest customerRequest) {
        return ResponseEntity.ok(customerService.updateCustomer(customerId, customerRequest));
    }

    /**
     * 특정 고객 정보를 조회합니다.
     *
     * @param customerId 조회할 고객의 ID
     * @return 조회된 고객 정보와 상태 코드 200(OK)
     */
    @Operation(summary = "고객 정보 조회", description = "특정 고객 정보를 조회합니다.")
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    /**
     * 모든 고객 정보를 조회합니다.
     *
     * @return 모든 고객 정보 목록과 상태 코드 200(OK)
     */
    @Operation(summary = "고객 정보 목록 조회", description = "모든 고객 정보 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    /**
     * 특정 고객 정보를 삭제합니다.
     *
     * @param customerId 삭제할 고객의 ID
     * @return 상태 코드 204(No Content)
     */
    @Operation(summary = "고객 정보 삭제", description = "특정 고객 정보를 삭제합니다.")
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}
