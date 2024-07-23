package com.yes255.yes255booksusersserver.application.service.scheduler;

import com.yes255.yes255booksusersserver.application.service.UserStateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class UserStateSchedulerTest {

    @Mock
    private UserStateService userStateService;

    @InjectMocks
    private UserStateScheduler userStateScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckInActiveUser() {
        // Given - no specific setup needed for this test

        // When
        userStateScheduler.checkInActiveUser();

        // Then
        verify(userStateService, times(1)).updateUserStateByUser();
    }
}