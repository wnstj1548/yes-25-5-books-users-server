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

    //@Scheduled(cron = "0 0 0 * * *")
    @Scheduled(fixedRate = 10000)
    public void scheduleBirthdayCoupons() {
        log.info("Scheduler started - Birthday coupon scheduler triggered");

        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();

        log.info("Today's date: month={}, day={}", month, day);

        List<User> usersWithBirthdayToday = userRepository.findUsersByBirthMonthAndDay(month, day);

        log.info("Number of users with birthday today: {}", usersWithBirthdayToday.size());
        if (usersWithBirthdayToday.isEmpty()) {
            log.info("No users with birthday today");
        } else {
            for (User user : usersWithBirthdayToday) {
                String redisKey = "birthday_coupon_issued_" + user.getUserId();
                Boolean isCouponAlreadyIssued = redisTemplate.hasKey(redisKey);

                if (Boolean.TRUE.equals(isCouponAlreadyIssued)) {
                    log.info("Birthday coupon already issued for user: {}", user.getUserId());
                    continue;
                }

                log.info("Sending birthday coupon for user: {}", user.getUserId());
                messageProducer.sendBirthdayCouponMessage(user.getUserId());

                redisTemplate.opsForValue().set(redisKey, "true", 1, TimeUnit.DAYS);
            }
        }

        log.info("Scheduler finished - Birthday coupon scheduler completed");
    }
}