package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.AddressService;
import com.yes255.yes255booksusersserver.persistance.domain.Address;
import com.yes255.yes255booksusersserver.presentation.dto.request.AddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.UpdateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.AddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.CreateAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.UpdateAddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/addresses")
@RequiredArgsConstructor
@RestController
public class AddressController {

    private final AddressService addressService;

    // 주소 등록
    @PostMapping
    public ResponseEntity<CreateAddressResponse> createAddress(@RequestBody CreateAddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.createAddress(addressRequest));
    }

    // 주소 수정
    @PutMapping("/{addressId}")
    public ResponseEntity<UpdateAddressResponse> updateAddress(@PathVariable Long addressId, @RequestBody UpdateAddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.updateAddress(addressId, addressRequest));
    }

    // 특정 주소 조회
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponse> findAddressById(@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.findAddressById(addressId));
    }

    // 주소 목록 조회
    @GetMapping
    public ResponseEntity<List<AddressResponse>> findAllAddresses() {
        return ResponseEntity.ok(addressService.findAllAddresses());
    }

    // 주소 삭제
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

    // 특정 주소 조회 (우편주소 OR 주소를 통해)
    @PostMapping("/find")
    public ResponseEntity<AddressResponse> findAddress(@RequestBody AddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.findByAddressZipOrAddressRaw(addressRequest));
    }
}
