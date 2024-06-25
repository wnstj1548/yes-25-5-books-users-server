package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.PointLogServiceImpl;
import com.yes255.yes255booksusersserver.persistance.domain.Point;
import com.yes255.yes255booksusersserver.persistance.domain.PointLog;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointLogRepository;
import com.yes255.yes255booksusersserver.presentation.dto.response.pointlog.PointLogResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointLogServiceImplTest {

    @Mock
    private JpaPointLogRepository pointLogRepository;

    @InjectMocks
    private PointLogServiceImpl pointLogService;

    private final Long userId = 1L;

    @BeforeEach
    void setup() {
        List<PointLog> mockPointLogs = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Point point = new Point(BigDecimal.valueOf(100 + i), null);
            PointLog pointLog = PointLog.builder()
                    .pointLogId((long) i)
                    .pointLogUpdatedAt(LocalDateTime.now().minusDays(i))
                    .pointLogUpdatedType("Type" + i)
                    .pointLogAmount(BigDecimal.valueOf(10 * i))
                    .point(point)
                    .build();
            mockPointLogs.add(pointLog);
        }

        when(pointLogRepository.findByPoint_User_UserIdOrderByPointLogUpdatedAtDesc(eq(userId), any(Pageable.class)))
                .thenReturn(mockPointLogs);
    }

    @Test
    @DisplayName("사용자의 포인트 로그 조회 - 성공")
    void testFindAllPointLogsByUserId_Success() {

        Pageable pageable = Pageable.unpaged(); // 페이징 처리 안 함

        List<PointLogResponse> pointLogResponses = pointLogService.findAllPointLogsByUserId(userId, pageable);

        assertEquals(10, pointLogResponses.size()); // 조회된 포인트 로그 개수가 10개인지 확인

        PointLogResponse firstPointLogResponse = pointLogResponses.getFirst();
        assertEquals(BigDecimal.valueOf(101), firstPointLogResponse.pointCurrent());
        assertEquals("Type1", firstPointLogResponse.pointLogUpdatedType());
        assertEquals(BigDecimal.valueOf(10), firstPointLogResponse.pointLogAmount());
        assertEquals(LocalDateTime.now().minusDays(1).toLocalDate(), firstPointLogResponse.pointLogUpdatedAt().toLocalDate());
        assertEquals(LocalDateTime.now().minusDays(1).getHour(), firstPointLogResponse.pointLogUpdatedAt().getHour());
        assertEquals(LocalDateTime.now().minusDays(1).getMinute(), firstPointLogResponse.pointLogUpdatedAt().getMinute());
    }
}
