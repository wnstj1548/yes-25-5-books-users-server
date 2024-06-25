package com.yes255.yes255booksusersserver.infrastructure.adaptor;

import org.hibernate.query.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "orderAdaptor", url = "${api.order-payment}/orders")
public interface OrderAdaptor {

//    @GetMapping("/orders/??")
//    List<PureOrderAmountResponse> getOrders();
}
