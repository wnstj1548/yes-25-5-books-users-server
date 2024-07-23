package com.yes255.yes255booksusersserver.infrastructure.adaptor;

import com.yes255.yes255booksusersserver.presentation.dto.response.OrderLogResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "orderAdaptor", url = "${api.order-payment}/orders")
public interface OrderAdaptor {

    @GetMapping("/logs")
    List<OrderLogResponse> getOrderLogs(@RequestParam LocalDate date);

    @GetMapping("/exist")
    boolean existOrderHistory(@RequestParam Long bookId);
}
