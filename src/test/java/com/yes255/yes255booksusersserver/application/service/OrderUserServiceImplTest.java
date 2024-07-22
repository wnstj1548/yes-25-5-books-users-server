package com.yes255.yes255booksusersserver.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yes255.yes255booksusersserver.application.service.impl.OrderUserServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.CustomerException;
import com.yes255.yes255booksusersserver.common.exception.PointException;
import com.yes255.yes255booksusersserver.common.exception.UserException;
import com.yes255.yes255booksusersserver.persistance.domain.Address;
import com.yes255.yes255booksusersserver.persistance.domain.Customer;
import com.yes255.yes255booksusersserver.persistance.domain.Point;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import com.yes255.yes255booksusersserver.persistance.domain.UserAddress;
import com.yes255.yes255booksusersserver.persistance.domain.UserGrade;
import com.yes255.yes255booksusersserver.persistance.repository.JpaCustomerRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserAddressRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadOrderUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadOrderUserInfoResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.ReadUserInfoResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OrderUserServiceImplTest {

    @Mock
    private JpaPointRepository pointRepository;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaCustomerRepository customerRepository;

    @Mock
    private JpaUserAddressRepository userAddressRepository;

    @InjectMocks
    private OrderUserServiceImpl orderUserService;

    private User testUser;
    private Customer testCustomer;
    private Point testPoint;
    private UserAddress testUserAddress;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .userId(1L)
                .userRole("MEMBER")
                .build();

        UserGrade testUserGrade = UserGrade.builder()
                .userGradeId(1L)
                .userGradeName("NORMAL")
                .build();

        testUser = User.builder()
                .userId(1L)
                .customer(testCustomer)
                .userName("Test User")
                .userEmail("test@example.com")
                .userPhone("010-1234-5678")
                .userRegisterDate(LocalDateTime.now())
                .userPassword("encodedPassword")
                .userGrade(testUserGrade)
                .build();

        testPoint = Point.builder()
                .pointCurrent(new BigDecimal(100))
                .user(testUser)
                .build();

        Address testAddress = Address.builder()
                .addressRaw("123 Main St")
                .addressZip("12345")
                .build();

        testUserAddress = UserAddress.builder()
                .userAddressId(1L)
                .user(testUser)
                .address(testAddress)
                .addressDetail("Apt 1")
                .addressName("Home")
                .addressBased(true)
                .build();

    }

    @Test
    @DisplayName("주문 서버로 주문 회원 정보 반환 - 성공")
    void testOrderUserInfo() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(testCustomer));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(pointRepository.findByUser_UserId(anyLong())).thenReturn(testPoint);

        ReadOrderUserInfoResponse response = orderUserService.orderUserInfo(1L);

        assertNotNull(response);
        assertEquals(1L, response.userId());
        assertEquals("Test User", response.name());
        assertEquals("test@example.com", response.email());
        assertEquals("010-1234-5678", response.phoneNumber());
        assertEquals("MEMBER", response.role());
        assertEquals(100, response.points());

        verify(customerRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(pointRepository, times(1)).findByUser_UserId(1L);
    }

    @Test
    @DisplayName("주문 서버로 주문 회원 정보 반환 - 실패 (고객을 찾을 수 없음)")
    void testOrderUserInfo_CustomerNotFound() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomerException exception = assertThrows(CustomerException.class, () -> {
            orderUserService.orderUserInfo(1L);
        });

        assertEquals("고객을 찾을 수 없습니다. 고객 ID : 1", exception.getErrorStatus().message());

        verify(customerRepository, times(1)).findById(1L);
        verify(userRepository, never()).findById(anyLong());
        verify(pointRepository, never()).findByUser_UserId(anyLong());
    }

    @Test
    @DisplayName("주문 서버로 주문 회원 정보 반환 - 실패 (회원을 찾을 수 없음)")
    void testOrderUserInfo_UserNotFound() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(testCustomer));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, () -> {
            orderUserService.orderUserInfo(1L);
        });

        assertEquals("회원이 존재하지 않습니다.", exception.getErrorStatus().message());

        verify(customerRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(pointRepository, never()).findByUser_UserId(anyLong());
    }

    @Test
    @DisplayName("주문 서버로 주문 회원 정보 반환 - 실패 (포인트를 찾을 수 없음)")
    void testOrderUserInfo_PointNotFound() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(testCustomer));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(pointRepository.findByUser_UserId(anyLong())).thenReturn(null);

        PointException exception = assertThrows(PointException.class, () -> {
            orderUserService.orderUserInfo(1L);
        });

        assertEquals("포인트가 존재하지 않습니다.", exception.getErrorStatus().message());

        verify(customerRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(pointRepository, times(1)).findByUser_UserId(1L);
    }

    @Test
    @DisplayName("주문 서버로 주문 회원 정보 반환 - 성공 (비회원 고객에 대한 처리)")
    void testOrderUserInfo_NoneMember() {
        testCustomer = Customer.builder()
                .userId(1L)
                .userRole("NONE_MEMBER")
                .build();

        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(testCustomer));

        ReadOrderUserInfoResponse response = orderUserService.orderUserInfo(1L);

        assertNotNull(response);
        assertEquals(1L, response.userId());
        assertEquals("", response.name());
        assertEquals("", response.email());
        assertEquals("", response.phoneNumber());
        assertEquals(0, response.points());
        assertEquals("NONE_MEMBER", response.role());

        verify(customerRepository, times(1)).findById(1L);
        verify(userRepository, never()).findById(anyLong());
        verify(pointRepository, never()).findByUser_UserId(anyLong());
    }

    @Test
    @DisplayName("주문 서버로 회원 주소 목록 반환 - 성공")
    void testGetUserAddresses() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserAddress> userAddressPage = new PageImpl<>(List.of(testUserAddress), pageable, 1);

        when(userAddressRepository.findByUserUserId(anyLong(), any(Pageable.class)))
                .thenReturn(userAddressPage);

        Page<ReadOrderUserAddressResponse> response = orderUserService.getUserAddresses(1L, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());

        ReadOrderUserAddressResponse addressResponse = response.getContent().getFirst();
        assertEquals(1L, addressResponse.userAddressId());
        assertEquals("123 Main St", addressResponse.addressRaw());
        assertEquals("Apt 1", addressResponse.addressDetail());
        assertEquals("Home", addressResponse.addressName());
        assertEquals("12345", addressResponse.zipCode());
        assertTrue(addressResponse.addressBased());

        verify(userAddressRepository, times(1)).findByUserUserId(1L, pageable);
    }

    @Test
    @DisplayName("주문 서버로 회원 정보 반환 - 성공")
    void testGetUserInfo() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(pointRepository.findByUser_UserId(anyLong())).thenReturn(testPoint);

        ReadUserInfoResponse response = orderUserService.getUserInfo(1L);

        assertNotNull(response);
        assertEquals(1L, response.userId());
        assertEquals("Test User", response.name());
        assertEquals(100, response.points());
        assertEquals(1L, response.gradeId());

        verify(userRepository, times(1)).findById(1L);
        verify(pointRepository, times(1)).findByUser_UserId(1L);
    }

    @Test
    @DisplayName("주문 서버로 회원 정보 반환 - 실패 (회원을 찾을 수 없음)")
    void testGetUserInfo_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, () -> {
            orderUserService.getUserInfo(1L);
        });

        assertEquals("회원이 존재하지 않습니다.", exception.getErrorStatus().message());

        verify(userRepository, times(1)).findById(1L);
        verify(pointRepository, never()).findByUser_UserId(anyLong());
    }

    @Test
    @DisplayName("주문 서버로 회원 정보 반환 - 실패 (포인트를 찾을 수 없음)")
    void testGetUserInfo_PointNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(pointRepository.findByUser_UserId(anyLong())).thenReturn(null);

        PointException exception = assertThrows(PointException.class, () -> {
            orderUserService.getUserInfo(1L);
        });

        assertEquals("포인트가 존재하지 않습니다.", exception.getErrorStatus().message());

        verify(userRepository, times(1)).findById(1L);
        verify(pointRepository, times(1)).findByUser_UserId(1L);
    }
}
