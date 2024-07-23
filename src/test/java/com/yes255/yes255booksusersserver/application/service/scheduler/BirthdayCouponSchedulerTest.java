package com.yes255.yes255booksusersserver.application.service.scheduler;

import com.yes255.yes255booksusersserver.application.service.queue.producer.MessageProducer;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class BirthdayCouponSchedulerTest {

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private MessageProducer messageProducer;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private BirthdayCouponScheduler birthdayCouponScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void scheduleBirthdayCoupons_noUsers() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();

        when(userRepository.findUsersByBirthMonth(month)).thenReturn(Collections.emptyList());

        birthdayCouponScheduler.scheduleBirthdayCoupons();

        verify(userRepository, times(1)).findUsersByBirthMonth(month);
        verifyNoInteractions(messageProducer);
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    void scheduleBirthdayCoupons_withUsers() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();

        User user = User.builder()
                .userId(1L)
                .build();

        when(userRepository.findUsersByBirthMonth(month)).thenReturn(Collections.singletonList(user));
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        birthdayCouponScheduler.scheduleBirthdayCoupons();

        verify(userRepository, times(1)).findUsersByBirthMonth(month);
        verify(messageProducer, times(1)).sendBirthdayCouponMessage(user.getUserId());
        verify(valueOperations, times(1)).set(anyString(), eq("true"), eq(31L), eq(TimeUnit.DAYS));
    }

    @Test
    void scheduleBirthdayCoupons_userAlreadyHasCoupon() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();

        User user = User.builder()
                .userId(1L)
                .build();

        when(userRepository.findUsersByBirthMonth(month)).thenReturn(Collections.singletonList(user));
        when(redisTemplate.hasKey(anyString())).thenReturn(true);

        birthdayCouponScheduler.scheduleBirthdayCoupons();

        verify(userRepository, times(1)).findUsersByBirthMonth(month);
        verify(messageProducer, never()).sendBirthdayCouponMessage(user.getUserId());
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }
}
