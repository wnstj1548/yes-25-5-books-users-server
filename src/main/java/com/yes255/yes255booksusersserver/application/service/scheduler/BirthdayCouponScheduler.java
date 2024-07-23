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

    @Scheduled(cron = "0 0 0 1 * *")
    public void scheduleBirthdayCoupons() {
        log.info("Scheduler started - Birthday coupon scheduler triggered");

        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();

        log.info("Current month: {}", currentMonth);

        List<User> usersWithBirthdayThisMonth = userRepository.findUsersByBirthMonth(currentMonth);

        log.info("Number of users with birthday this month: {}", usersWithBirthdayThisMonth.size());
        if (usersWithBirthdayThisMonth.isEmpty()) {
            log.info("No users with birthday this month");
        } else {
            for (User user : usersWithBirthdayThisMonth) {
                String redisKey = "birthday_coupon_issued_" + user.getUserId();
                Boolean isCouponAlreadyIssued = redisTemplate.hasKey(redisKey);

                if (Boolean.TRUE.equals(isCouponAlreadyIssued)) {
                    log.info("Birthday coupon already issued for user: {}", user.getUserId());
                    continue;
                }

                log.info("Sending birthday coupon for user: {}", user.getUserId());
                messageProducer.sendBirthdayCouponMessage(user.getUserId());

                redisTemplate.opsForValue().set(redisKey, "true", 31, TimeUnit.DAYS);
            }
        }

        log.info("Scheduler finished - Birthday coupon scheduler completed");
    }
}
