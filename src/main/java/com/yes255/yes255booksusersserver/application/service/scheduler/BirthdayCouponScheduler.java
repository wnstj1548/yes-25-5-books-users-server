package com.yes255.yes255booksusersserver.application.service.scheduler;

import com.yes255.yes255booksusersserver.application.service.queue.producer.MessageProducer;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class BirthdayCouponScheduler {

    private final JpaUserRepository userRepository;
    private final MessageProducer messageProducer;
    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleBirthdayCoupons() {

        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();


        List<User> usersWithBirthdayToday = userRepository.findUsersByBirthMonthAndDay(month, day);

        if (usersWithBirthdayToday.isEmpty()) {
        } else {
            for (User user : usersWithBirthdayToday) {
                String redisKey = "birthday_coupon_issued_" + user.getUserId();
                Boolean isCouponAlreadyIssued = redisTemplate.hasKey(redisKey);

                if (Boolean.TRUE.equals(isCouponAlreadyIssued)) {
                    continue;
                }

                messageProducer.sendBirthdayCouponMessage(user.getUserId());

                redisTemplate.opsForValue().set(redisKey, "true", 31, TimeUnit.DAYS);
            }
        }

    }
}