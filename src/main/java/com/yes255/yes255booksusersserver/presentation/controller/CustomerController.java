package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.CustomerService;
import com.yes255.yes255booksusersserver.persistance.domain.Customer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "고객 API", description = "고객 관련 API 입니다.")
@RequestMapping("/customers")
@RequiredArgsConstructor
@RestController
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "고객 정보 생성", description = "고객을 생성합니다.")
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.createCustomer(customer));
    }

    @Operation(summary = "고객 정보 수정", description = "특정 고객 정보를 수정합니다.")
    @PutMapping("/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long customerId, @RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.updateCustomer(customerId, customer));
    }

    @Operation(summary = "고객 정보 조회", description = "특정 고객 정보를 조회합니다.")
    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    @Operation(summary = "고객 정보 목록 조회", description = "모든 고객 정보 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @Operation(summary = "고객 정보 삭제", description = "특정 고객 정보를 삭제합니다.")
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}
