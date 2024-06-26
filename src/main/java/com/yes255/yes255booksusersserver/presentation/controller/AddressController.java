package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.AddressService;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.AddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.CreateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.address.UpdateAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.AddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.CreateAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.address.UpdateAddressResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 주소 관련 API를 제공하는 AddressController
 */

@Tag(name = "주소 API", description = "주소 관련 API 입니다.")
@RequestMapping("/addresses")
@RequiredArgsConstructor
@RestController
public class AddressController {

    private final AddressService addressService;

    /**
     * 주소를 등록합니다.
     *
     * @param addressRequest 주소 등록 요청 객체
     * @return 등록된 주소 정보를 포함한 응답 객체
     */
    @Operation(summary = "주소 등록", description = "주소를 등록합니다.")
    @PostMapping
    public ResponseEntity<CreateAddressResponse> createAddress(@RequestBody CreateAddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.createAddress(addressRequest));
    }

    /**
     * 특정 주소를 조회합니다.
     *
     * @param addressId 조회할 주소의 ID
     * @return 조회된 주소 정보를 포함한 응답 객체
     */
    @Operation(summary = "주소 수정", description = "특정 주소를 수정합니다.")
    @PutMapping("/{addressId}")
    public ResponseEntity<UpdateAddressResponse> updateAddress(@PathVariable Long addressId, @RequestBody UpdateAddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.updateAddress(addressId, addressRequest));
    }

    /**
     * 모든 주소 목록을 조회합니다.
     *
     * @return 모든 주소 정보를 포함한 응답 객체 목록
     */
    @Operation(summary = "주소 조회", description = "특정 주소를 조회합니다.")
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponse> findAddressById(@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.findAddressById(addressId));
    }


    /**
     * 모든 주소 목록을 조회합니다.
     *
     * @return 모든 주소 정보를 포함한 응답 객체 목록
     */
    @Operation(summary = "주소 목록 조회", description = "모든 주소 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<AddressResponse>> findAllAddresses() {
        return ResponseEntity.ok(addressService.findAllAddresses());
    }

    /**
     * 특정 주소를 삭제합니다.
     *
     * @param addressId 삭제할 주소의 ID
     * @return No Content 상태의 응답
     */
    @Operation(summary = "주소 삭제", description = "특정 주소를 삭제합니다.")
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 우편주소나 일반 주소를 통해 주소를 조회합니다.
     *
     * @param addressRequest 주소 조회 요청 객체
     * @return 조회된 주소 정보를 포함한 응답 객체
     */
    @Operation(summary = "특정 주소 조회", description = "우편주소나 일반 주소를 통해 주소를 조회합니다.")
    @PostMapping("/find")
    public ResponseEntity<AddressResponse> findAddress(@RequestBody AddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.findByAddressZipOrAddressRaw(addressRequest));
    }
}
