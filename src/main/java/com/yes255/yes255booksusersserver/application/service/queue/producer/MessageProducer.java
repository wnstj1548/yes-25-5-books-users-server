package com.yes255.yes255booksusersserver.application.service.queue.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendBirthdayCouponMessage(Long userId) {
        rabbitTemplate.convertAndSend("yes255CouponExchange", "yes255BirthdayCouponRoutingKey", userId);
    }

    public void sendWelcomeCouponMessage(Long userId) {
        rabbitTemplate.convertAndSend("yes255CouponExchange", "yes255WelcomeCouponRoutingKey", userId);
    }
}