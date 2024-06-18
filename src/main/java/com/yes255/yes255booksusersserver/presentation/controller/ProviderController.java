//package com.yes255.yes255booksusersserver.presentation.controller;
//
//import com.yes255.yes255booksusersserver.application.service.ProviderService;
//import com.yes255.yes255booksusersserver.persistance.domain.Provider;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RequestMapping("/providers")
//@RequiredArgsConstructor
//@RestController
//public class ProviderController {
//
//    private ProviderService providerService;
//
//    @PostMapping
//    public ResponseEntity<Provider> createProvider(@RequestBody Provider provider) {
//        return ResponseEntity.ok(providerService.createProvider(provider));
//    }
//
//    @PutMapping("/{providerId}")
//    public ResponseEntity<Provider> updateProvider(@PathVariable Long providerId, @RequestBody Provider provider) {
//        return ResponseEntity.ok(providerService.updateProvider(providerId, provider));
//    }
//
//    @GetMapping("/{providerId}")
//    public ResponseEntity<Provider> getProviderById(@PathVariable Long providerId) {
//        return ResponseEntity.ok(providerService.getProviderById(providerId));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Provider>> getAllProviders() {
//        return ResponseEntity.ok(providerService.getAllProviders());
//    }
//
//    @DeleteMapping("/{providerId}")
//    public ResponseEntity<Void> deleteProvider(@PathVariable Long providerId) {
//        providerService.deleteProvider(providerId);
//        return ResponseEntity.noContent().build();
//    }
//}
