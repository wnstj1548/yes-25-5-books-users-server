package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "address_zip", length = 30)
    private String addressZip;

    @NotNull(message = "주소는 필수입니다.")
    @Column(name = "address_raw", nullable = false)
    private String addressRaw;

    @Builder
    private Address(Long addressId, String addressZip, String addressRaw){
        this.addressId = addressId;
        this.addressZip = addressZip;
        this.addressRaw = addressRaw;

    }
}


