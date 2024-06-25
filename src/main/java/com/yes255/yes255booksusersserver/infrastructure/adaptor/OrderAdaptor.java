package com.yes255.yes255booksusersserver.infrastructure.adaptor;

import org.hibernate.query.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "orderAdaptor", url = "http://localhost:8071") //"http://133.186.153.195:8071")
public interface OrderAdaptor {

//    @GetMapping("/orders/??")
//    List<PureOrderAmountResponse> getOrders();
}
