package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @Column(name = "address_name")
    private String addressName;

    @Column(name = "address_detail")
    private String addressDetail;

    @NotNull(message = "기본 여부는 필수입니다.")
    @Column(name = "address_based", nullable = false)
    private boolean addressBased;

    @ManyToOne(optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public UserAddress(Long userAddressId, String addressName, String addressDetail,
                       boolean addressBased, Address address, User user) {

        this.userAddressId = userAddressId;
        this.addressName = addressName;
        this.addressDetail = addressDetail;
        this.addressBased = addressBased;
        this.address = address;
        this.user = user;
    }



}
