package com.yes255.yes255booksusersserver.application.service.queue.consumer;

import com.yes255.yes255booksusersserver.application.service.CouponUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponConsumer {

    private static final Logger log = LoggerFactory.getLogger(CouponConsumer.class);
    private final CouponUserService couponUserService;

    @RabbitListener(queues = "welcomeQueue")
    public void handleWelcomeCouponMessage(Long userId) {
        couponUserService.createCouponUserForWelcome(userId);
    }

    @RabbitListener(queues = "birthdayQueue")
    public void handleBirthdayCouponMessage(Long userId) {
        log.info("Received BIRTHDAY COUPON MESSAGE");
        couponUserService.createCouponUserForBirthday(userId);
    }
}