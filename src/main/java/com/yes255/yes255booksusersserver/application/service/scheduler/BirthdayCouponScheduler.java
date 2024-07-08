package com.yes255.yes255booksusersserver.application.service.scheduler;

import com.yes255.yes255booksusersserver.application.service.CouponUserService;
import com.yes255.yes255booksusersserver.application.service.queue.producer.MessageProducer;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCouponUserRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BirthdayCouponScheduler {

    private final JpaUserRepository userRepository;
    private final MessageProducer messageProducer; // MessageProducer 추가

    @Scheduled(fixedRate = 180000) // 매일 자정에 실행
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
                log.info("Sending birthday coupon for user: {}", user.getUserId());
                // 래빗앰큐를 통해 메시지 전송
                messageProducer.sendBirthdayCouponMessage(user.getUserId());
            }
        }

        log.info("Scheduler finished - Birthday coupon scheduler completed");
    }
}