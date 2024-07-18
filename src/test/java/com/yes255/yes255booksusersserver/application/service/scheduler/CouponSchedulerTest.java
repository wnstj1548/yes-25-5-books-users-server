package com.yes255.yes255booksusersserver.application.service.scheduler;

import com.yes255.yes255booksusersserver.application.service.CouponUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class CouponSchedulerTest {

    @Mock
    private CouponUserService couponUserService;

    @InjectMocks
    private CouponScheduler couponScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckExpiredAndDeleteExpiredCoupons() {
        // Given - no specific setup needed for this test

        // When
        couponScheduler.checkExpired();

        // Then
        verify(couponUserService, times(1)).checkExpiredCoupon();
        verify(couponUserService, times(1)).deleteExpiredCoupons();
    }
}