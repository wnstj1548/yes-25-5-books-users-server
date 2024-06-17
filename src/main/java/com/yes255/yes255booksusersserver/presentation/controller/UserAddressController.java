package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserAddressService;
import com.yes255.yes255booksusersserver.persistance.domain.Address;
import com.yes255.yes255booksusersserver.persistance.domain.UserAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/user-addresses")
@RequiredArgsConstructor
@RestController
public class UserAddressController {
//
//    private final UserAddressService userAddressService;
///*
//* Address createAddress(Address address);
//    Address updateAddress(Long addressId, Address address);
//    Address getAddressById(Long addressId);
//    List<Address> getAllAddresses();
//    void deleteAddress(Long addressId);
//* */
//    @PostMapping
//    public ResponseEntity<Address> createUserAddress(@RequestBody Address Address) {
//        return ResponseEntity.ok(userAddressService.createAddress(Address));
//    }
//
//    @PutMapping("/{userAddressId}")
//    public ResponseEntity<Address> updateUserAddress(@PathVariable Long AddressId, @RequestBody Address Address) {
//        return ResponseEntity.ok(userAddressService.updateAddress(AddressId, Address));
//    }
//
//    @GetMapping("/{userAddressId}")
//    public ResponseEntity<Address> getUserAddressById(@PathVariable Long AddressId) {
//        return ResponseEntity.ok(userAddressService.getAddressById(AddressId));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Address>> getAllUserAddresses() {
//        return ResponseEntity.ok(userAddressService.getAllAddresses());
//    }
//
//    @DeleteMapping("/{userAddressId}")
//    public ResponseEntity<Void> deleteUserAddress(@PathVariable Long AddressId) {
//        userAddressService.deleteAddress(AddressId);
//        return ResponseEntity.noContent().build();
//    }
}
