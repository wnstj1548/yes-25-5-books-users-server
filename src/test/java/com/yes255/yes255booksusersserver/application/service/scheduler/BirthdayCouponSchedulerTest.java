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
    private ValueOperations<String, String> valueOperations; // Mock ValueOperations

    @InjectMocks
    private BirthdayCouponScheduler birthdayCouponScheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock RedisTemplate opsForValue() method
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void scheduleBirthdayCoupons_noUsers() {
        // Given
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();

        when(userRepository.findUsersByBirthMonthAndDay(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        // When
        birthdayCouponScheduler.scheduleBirthdayCoupons();

        // Then
        verify(userRepository, times(1)).findUsersByBirthMonthAndDay(month, day);
        verifyNoInteractions(messageProducer);
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    void scheduleBirthdayCoupons_withUsers() {
        // Given
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();

        User user = User.builder()
                .userId(1L)
                .build();

        when(userRepository.findUsersByBirthMonthAndDay(anyInt(), anyInt())).thenReturn(Collections.singletonList(user));
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        // When
        birthdayCouponScheduler.scheduleBirthdayCoupons();

        // Then
        verify(userRepository, times(1)).findUsersByBirthMonthAndDay(month, day);
        verify(messageProducer, times(1)).sendBirthdayCouponMessage(user.getUserId());
        verify(valueOperations, times(1)).set(anyString(), eq("true"), eq(31L), eq(TimeUnit.DAYS));
    }
}