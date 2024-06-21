package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.AddressService;
import com.yes255.yes255booksusersserver.persistance.domain.Address;
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

    @PostMapping
    public ResponseEntity<CreateAddressResponse> createAddress(@RequestBody CreateAddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.createAddress(addressRequest));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<UpdateAddressResponse> updateAddress(@PathVariable Long addressId, @RequestBody UpdateAddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.updateAddress(addressId, addressRequest));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.findAddressById(addressId));
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddresses() {
        return ResponseEntity.ok(addressService.findAllAddresses());
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }
}
