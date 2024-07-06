package com.yes255.yes255booksusersserver.application.service.queue.producer;

import com.yes255.yes255booksusersserver.infrastructure.adaptor.CouponAdaptor;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendBirthdayCouponMessage(Long userId) {
        rabbitTemplate.convertAndSend("couponExchange", "birthdayCoupon", userId);
    }

    public void sendWelcomeCouponMessage(Long userId) {
        rabbitTemplate.convertAndSend("couponExchange", "welcomeCoupon", userId);
    }


}