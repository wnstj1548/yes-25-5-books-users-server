package com.yes255.yes255booksusersserver.application.service.impl;


import com.yes255.yes255booksusersserver.application.service.OrderProductService;
import com.yes255.yes255booksusersserver.persistance.repository.JpaOrderProductRepository;
import com.yes255.yes255booksusersserver.persistance.domain.OrderProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderProductServiceImpl implements OrderProductService {

    private final JpaOrderProductRepository orderProductRepository;

    @Override
    public OrderProduct createOrderProduct(OrderProduct orderProduct) {
        log.info("Creating order product with Book ID: {}", orderProduct.getBookId());
        return orderProductRepository.save(orderProduct);
    }

    @Override
    public OrderProduct updateOrderProduct(Long orderProductId, OrderProduct orderProductDetails) {
        OrderProduct existingOrderProduct = orderProductRepository.findById(orderProductId)
                .orElseThrow(() -> new IllegalStateException("OrderProduct not found with id " + orderProductId));

        OrderProduct updatedOrderProduct = OrderProduct.builder()
                .orderItemId(existingOrderProduct.getOrderItemId())
                .orderItemQuantity(orderProductDetails.getOrderItemQuantity() != null ? orderProductDetails.getOrderItemQuantity() : existingOrderProduct.getOrderItemQuantity())
                .orderItemPrice(orderProductDetails.getOrderItemPrice() != null ? orderProductDetails.getOrderItemPrice() : existingOrderProduct.getOrderItemPrice())
                .orderItemCreatedAt(existingOrderProduct.getOrderItemCreatedAt())
                .orderItemUpdatedAt(java.time.LocalDateTime.now()) // Update the updated time
                .bookId(orderProductDetails.getBookId() != null ? orderProductDetails.getBookId() : existingOrderProduct.getBookId())
               // .order(existingOrderProduct.getOrder()) // Maintain the same order association
                .build();

        log.info("Updating order product with ID: {}", orderProductId);
        return orderProductRepository.save(updatedOrderProduct);
    }

    @Override
    public OrderProduct getOrderProductById(Long orderProductId) {
        log.info("Fetching order product with ID: {}", orderProductId);
        return orderProductRepository.findById(orderProductId)
                .orElseThrow(() -> new IllegalStateException("OrderProduct not found with id " + orderProductId));
    }

    @Override
    public List<OrderProduct> getAllOrderProducts() {
        log.info("Fetching all order products");
        return orderProductRepository.findAll();
    }

    @Override
    public void deleteOrderProduct(Long orderProductId) {
        log.info("Deleting order product with ID: {}", orderProductId);
        orderProductRepository.deleteById(orderProductId);
    }
}
