package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.AddressService;
import com.yes255.yes255booksusersserver.persistance.domain.Address;
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
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        return ResponseEntity.ok(addressService.createAddress(address));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long addressId, @RequestBody Address address) {
        return ResponseEntity.ok(addressService.updateAddress(addressId, address));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<Address> getAddressById(@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.getAddressById(addressId));
    }

    @GetMapping
    public ResponseEntity<List<Address>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }
}
