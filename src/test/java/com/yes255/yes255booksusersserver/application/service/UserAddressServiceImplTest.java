package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.application.service.impl.PointServiceImpl;
import com.yes255.yes255booksusersserver.application.service.impl.UserAddressServiceImpl;
import com.yes255.yes255booksusersserver.common.exception.UserAddressException;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.JpaAddressRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaPointRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserAddressRepository;
import com.yes255.yes255booksusersserver.persistance.repository.JpaUserRepository;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.CreateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.UpdateAddressBasedRequest;
import com.yes255.yes255booksusersserver.presentation.dto.request.useraddress.UpdateUserAddressRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.CreateUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.UpdateUserAddressResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.useraddress.UserAddressResponse;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAddressServiceImplTest {

    @Mock
    private JpaUserAddressRepository userAddressRepository;

    @Mock
    private JpaAddressRepository addressRepository;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaPointRepository pointRepository;

    @InjectMocks
    private UserAddressServiceImpl userAddressService;

    @InjectMocks
    private PointServiceImpl pointService;

    private User testUser;
    private Address testAddress;
    private UserAddress testUserAddress;

    @BeforeEach
    void setup() {
        // Customer 설정
        Customer testCustomer = Customer.builder()
                .userId(1L)
                .userRole("USER")
                .build();

        // Provider 설정
        Provider testProvider = Provider.builder()
                .providerId(1L)
                .providerName("TestProvider")
                .build();

        // UserState 설정
        UserState testUserState = UserState.builder()
                .userStateId(1L)
                .userStateName("Active")
                .build();

        // UserGrade 설정
        UserGrade testUserGrade = UserGrade.builder()
                .userGradeId(1L)
                .userGradeName("Gold")
                .build();

        testUser = User.builder()
                .customer(testCustomer)
                .userName("TestUser")
                .userPhone("010-1234-5678")
                .userEmail("testuser@example.com")
                .userBirth(LocalDate.of(1990, 1, 1))
                .userRegisterDate(LocalDateTime.now())
                .provider(testProvider)
                .userState(testUserState)
                .userGrade(testUserGrade)
                .userPassword("password")
                .build();

        testAddress = Address.builder()
                .addressId(1L)
                .addressZip("12345")
                .addressRaw("Test Address")
                .build();

        testUserAddress = UserAddress.builder()
                .userAddressId(1L)
                .addressName("Home")
                .addressDetail("Detail")
                .addressBased(true)
                .address(testAddress)
                .user(testUser)
                .build();


        Point testPoint = Point.builder()
                .pointCurrent(BigDecimal.valueOf(100))
                .user(testUser)
                .build();

    }

    @Test
    @DisplayName("주소 생성 - 성공")
    void testCreateAddress_Success() {
        CreateUserAddressRequest request = CreateUserAddressRequest.builder()
                .addressZip("12345")
                .addressRaw("Test Address")
                .addressName("Home")
                .addressDetail("Detail")
                .addressBased(true)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(addressRepository.findAddressByAddressRawAndAddressZip(anyString(), anyString())).thenReturn(null);
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(testUserAddress);

        CreateUserAddressResponse response = userAddressService.createAddress(1L, request);

        assertNotNull(response);
        assertEquals("12345", response.addressZip());
        assertEquals("Test Address", response.addressRaw());
        assertEquals("Home", response.addressName());
        assertEquals("Detail", response.addressDetail());
        assertTrue(response.addressBased());
    }

    @Test
    @DisplayName("주소 업데이트 - 성공")
    void testUpdateAddress_Success() {
        UpdateUserAddressRequest request = UpdateUserAddressRequest.builder()
                .addressZip("54321")
                .addressRaw("New Address")
                .addressName("Work")
                .addressDetail("New Detail")
                .addressBased(false)
                .build();

        when(userAddressRepository.findById(anyLong())).thenReturn(Optional.of(testUserAddress));
        when(addressRepository.findById(anyLong())).thenReturn(Optional.of(testAddress));
        when(addressRepository.findAddressByAddressRawAndAddressZip(anyString(), anyString())).thenReturn(null);
        when(userAddressRepository.save(any(UserAddress.class))).thenReturn(testUserAddress);

        UpdateUserAddressResponse response = userAddressService.updateAddress(1L, 1L, request);

        assertNotNull(response);
        assertEquals("54321", response.addressZip());
        assertEquals("New Address", response.addressRaw());
        assertEquals("Work", response.addressName());
        assertEquals("New Detail", response.addressDetail());
        assertFalse(response.addressBased());
    }

    @Test
    @DisplayName("주소 조회 - 성공")
    void testFindAddressById_Success() {
        when(userAddressRepository.findByUserAddressIdAndUserUserId(anyLong(), anyLong())).thenReturn(testUserAddress);

        UserAddressResponse response = userAddressService.findAddressById(1L, 1L);

        assertNotNull(response);
        assertEquals(1L, response.userAddressId());
        assertEquals(1L, response.addressId());
        assertEquals("12345", response.addressZip());
        assertEquals("Test Address", response.addressRaw());
        assertEquals("Home", response.addressName());
        assertEquals("Detail", response.addressDetail());
        assertTrue(response.addressBased());
        assertEquals(1L, response.userId());
    }

    @Test
    @DisplayName("주소 조회 - 실패")
    void testFindAddressById_Failure() {
        when(userAddressRepository.findByUserAddressIdAndUserUserId(anyLong(), anyLong())).thenReturn(null);

        assertThrows(UserAddressException.class, () ->
                userAddressService.findAddressById(1L, 1L));
    }

    @Test
    @DisplayName("주소 삭제 - 성공")
    void testDeleteAddress_Success() {
        doNothing().when(userAddressRepository).deleteById(anyLong());

        userAddressService.deleteAddress(1L, 1L);
    }

    @Test
    @DisplayName("주소 목록 조회 - 성공")
    void testFindAllAddresses_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserAddress> userAddressPage = new PageImpl<>(List.of(testUserAddress), pageable, 1);

        when(userAddressRepository.findByUserUserId(anyLong(), any(Pageable.class))).thenReturn(userAddressPage);

        Page<UserAddressResponse> responses = userAddressService.findAllAddresses(1L, pageable);

        assertNotNull(responses);
        assertEquals(1, responses.getTotalElements());

        UserAddressResponse response = responses.getContent().get(0);
        assertEquals(1L, response.userAddressId());
        assertEquals(1L, response.addressId());
        assertEquals("12345", response.addressZip());
        assertEquals("Test Address", response.addressRaw());
        assertEquals("Home", response.addressName());
        assertEquals("Detail", response.addressDetail());
        assertTrue(response.addressBased());
        assertEquals(1L, response.userId());
    }

//    @Test
//    @DisplayName("주소 목록 조회 - 실패")
//    void testFindAllAddresses_Failure() {
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<UserAddress> emptyPage = new PageImpl<>(List.of(), pageable, 0);
//
//        when(userAddressRepository.findByUserUserId(anyLong(), any(Pageable.class))).thenReturn(emptyPage);
//
//        assertThrows(UserAddressException.class, () ->
//                userAddressService.findAllAddresses(1L, pageable));
//    }

    @Test
    @DisplayName("기본 배송지 업데이트 - 성공")
    void testUpdateAddressBased_Success() {
        UpdateAddressBasedRequest request = UpdateAddressBasedRequest.builder()
                .addressBased(true)
                .build();

        UserAddress anotherUserAddress = UserAddress.builder()
                .userAddressId(2L)
                .addressName("Office")
                .addressDetail("Office Detail")
                .addressBased(false)
                .address(testAddress)
                .user(testUser)
                .build();

        when(userAddressRepository.findById(anyLong())).thenReturn(Optional.of(testUserAddress));
        when(userAddressRepository.findAll()).thenReturn(List.of(testUserAddress, anotherUserAddress));

        userAddressService.updateAddressBased(1L, 1L, request);

        assertTrue(testUserAddress.isAddressBased());
        assertFalse(anotherUserAddress.isAddressBased());
    }

    @Test
    @DisplayName("기본 배송지 업데이트 - 실패 (주소 없음)")
    void testUpdateAddressBased_Failure_NoAddress() {
        UpdateAddressBasedRequest request = UpdateAddressBasedRequest.builder()
                .addressBased(true)
                .build();

        when(userAddressRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserAddressException.class, () ->
                userAddressService.updateAddressBased(1L, 1L, request));
    }

    @Test
    @DisplayName("기본 배송지 업데이트 - 실패 (주소 리스트 없음)")
    void testUpdateAddressBased_Failure_NoAddressList() {
        UpdateAddressBasedRequest request = UpdateAddressBasedRequest.builder()
                .addressBased(true)
                .build();

        when(userAddressRepository.findById(anyLong())).thenReturn(Optional.of(testUserAddress));
        when(userAddressRepository.findAll()).thenReturn(List.of());

        assertThrows(UserAddressException.class, () ->
                userAddressService.updateAddressBased(1L, 1L, request));
    }
}
