package com.yes255.yes255booksusersserver.application.service.scheduler;

import com.yes255.yes255booksusersserver.application.service.UserGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserGradeScheduler {

    private final UserGradeService userGradeService;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void checkMonthlyGrades() {
        userGradeService.updateMonthlyGrades();
    }
}
