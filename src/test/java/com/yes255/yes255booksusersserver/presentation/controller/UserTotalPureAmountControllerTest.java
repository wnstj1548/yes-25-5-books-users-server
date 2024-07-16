package com.yes255.yes255booksusersserver.presentation.controller;

import com.yes255.yes255booksusersserver.application.service.UserTotalPureAmountService;
import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadPurePriceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserTotalPureAmountControllerTest {

    @Mock
    private UserTotalPureAmountService userTotalPureAmountService;

    @InjectMocks
    private UserTotalPureAmountController userTotalPureAmountController;

    private JwtUserDetails jwtUserDetails;
    private ReadPurePriceResponse readPurePriceResponse;

    @BeforeEach
    void setUp() {
        jwtUserDetails = JwtUserDetails.of(1L, "USER", "accessToken", "refreshToken");

        readPurePriceResponse = ReadPurePriceResponse.builder()
                .purePrice(BigDecimal.valueOf(1000.00))
                .recordedAt(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("순수 가격 조회 - 성공")
    void testGetPurePrice() {
        when(userTotalPureAmountService.findUserTotalPureAmountByUserId(anyLong()))
                .thenReturn(readPurePriceResponse);

        ResponseEntity<ReadPurePriceResponse> response = userTotalPureAmountController.getPurePrice(jwtUserDetails);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(readPurePriceResponse);
    }
}
