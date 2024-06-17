package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.OrderProduct;

import java.util.List;

public interface OrderProductService {
    OrderProduct createOrderProduct(OrderProduct orderProduct);
    OrderProduct updateOrderProduct(Long orderProductId, OrderProduct orderProduct);
    OrderProduct getOrderProductById(Long orderProductId);
    List<OrderProduct> getAllOrderProducts();
    void deleteOrderProduct(Long orderProductId);
}
