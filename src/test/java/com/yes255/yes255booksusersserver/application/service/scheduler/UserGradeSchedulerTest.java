package com.yes255.yes255booksusersserver.application.service.scheduler;

import com.yes255.yes255booksusersserver.application.service.UserGradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class UserGradeSchedulerTest {

    @Mock
    private UserGradeService userGradeService;

    @InjectMocks
    private UserGradeScheduler userGradeScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckMonthlyGrades() {
        // Given - no specific setup needed for this test

        // When
        userGradeScheduler.checkMonthlyGrades();

        // Then
        verify(userGradeService, times(1)).updateMonthlyGrades();
    }
}