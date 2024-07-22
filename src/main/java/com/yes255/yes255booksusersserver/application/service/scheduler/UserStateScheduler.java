package com.yes255.yes255booksusersserver.application.service.scheduler;

import com.yes255.yes255booksusersserver.application.service.UserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStateScheduler {

    private final UserStateService userStateService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkInActiveUser() {
        userStateService.updateUserStateByUser();
    }
}
