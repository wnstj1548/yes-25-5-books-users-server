package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_address")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_address_id")
    private Long userAddressId;

    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "address_name")
    private String addressName;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "address_based")
    private boolean addressBased;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "address_id", insertable = false, updatable = false)
    private Address address;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Builder
    public UserAddress(Long userAddressId,
                       Long addressId,
                       String addressName,
                       String addressDetail,
                       boolean addressBased,
                       Long userId,
                       Address address,
                       User user
                       ){
        this.userAddressId = userAddressId;
        this.addressId = addressId;
        this.addressName = addressName;
        this.addressDetail = addressDetail;
        this.addressBased = addressBased;
        this.userId = userId;
        this.address = address;
        this.user = user;

    }



}
