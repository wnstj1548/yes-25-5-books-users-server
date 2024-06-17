package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "address_zip")
    private String addressZip;

    @Column(name = "address_raw")
    private String addressRaw;

    @Builder
    private Address(Long addressId, String addressZip, String addressRaw){
        this.addressId = addressId;
        this.addressZip = addressZip;
        this.addressRaw = addressRaw;

    }

    //@OneToMany(mappedBy = "address")
    //private List<UserAddress> userAddresses = new ArrayList<>();
}


