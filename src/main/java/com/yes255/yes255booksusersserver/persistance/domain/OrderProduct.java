package com.yes255.yes255booksusersserver.persistance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "order_product")
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    private Integer orderItemQuantity;

    private Integer orderItemPrice;

    private LocalDateTime orderItemCreatedAt;

    private LocalDateTime orderItemUpdatedAt;

    private Long bookId;

    private Long orderId;

    @Builder
    public OrderProduct(Long orderItemId,
                        Integer orderItemQuantity,
                        Integer orderItemPrice,
                        LocalDateTime orderItemCreatedAt,
                        LocalDateTime orderItemUpdatedAt,
                        Long bookId,
                        Long orderId){

        this.orderItemId = orderItemId;
        this.orderItemQuantity = orderItemQuantity;
        this.orderItemPrice = orderItemPrice;
        this.orderItemCreatedAt = orderItemCreatedAt;
        this.orderItemUpdatedAt = orderItemUpdatedAt;
        this.bookId = bookId;
        this.orderId = orderId;




    }

    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "order_id", nullable = false) // 실제 DB 컬럼 이름 지정
    //private Order order; // Order 엔티티와의 연결을 나타냅니다.



}
