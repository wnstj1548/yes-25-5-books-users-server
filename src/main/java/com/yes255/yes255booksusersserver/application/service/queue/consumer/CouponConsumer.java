package com.yes255.yes255booksusersserver.application.service.queue.consumer;

import com.yes255.yes255booksusersserver.application.service.CouponUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponConsumer {

    private static final Logger log = LoggerFactory.getLogger(CouponConsumer.class);
    private final CouponUserService couponUserService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "yes255WelcomeQueue")
    public void handleWelcomeCouponMessage(Long userId) {
        log.info("Received WELCOME COUPON MESSAGE for userId: {}", userId);
        couponUserService.createCouponUserForWelcome(userId);
    }

    @RabbitListener(queues = "yes255BirthdayQueue")
    public void handleBirthdayCouponMessage(Long userId) {
        log.info("Received BIRTHDAY COUPON MESSAGE for userId: {}", userId);
        couponUserService.createCouponUserForBirthday(userId);
    }

    @RabbitListener(queues = "yes255.dead.welcomeCoupon")
    public void handleDeadWelcomeCouponMessage(Long userId) {
        log.info("Handling DEAD WELCOME COUPON MESSAGE for userId: {}", userId);
        try {
            log.info("Retrying WELCOME COUPON MESSAGE for userId: {}", userId);
            rabbitTemplate.convertAndSend("yes255CouponExchange", "yes255WelcomeCouponRoutingKey", userId);
        } catch (Exception e) {
            log.error("Failed to process DEAD WELCOME COUPON MESSAGE for userId: {}", userId, e);
        }
    }

    @RabbitListener(queues = "yes255.dead.birthdayCoupon")
    public void handleDeadBirthdayCouponMessage(Long userId) {
        log.info("Handling DEAD BIRTHDAY COUPON MESSAGE for userId: {}", userId);
        try {
            log.info("Retrying BIRTHDAY COUPON MESSAGE for userId: {}", userId);
            rabbitTemplate.convertAndSend("yes255CouponExchange", "yes255BirthdayCouponRoutingKey", userId);
        } catch (Exception e) {
            log.error("Failed to process DEAD BIRTHDAY COUPON MESSAGE for userId: {}", userId, e);
        }
    }
}