package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.UserServices;
import com.yes255.yes255booksusersserver.persistance.domain.*;
import com.yes255.yes255booksusersserver.persistance.repository.*;
import com.yes255.yes255booksusersserver.presentation.dto.request.CreateUserRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.CreateUserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServicesImpl implements UserServices {

    private final JpaUserRepository userRepository;

    private final JpaCustomerRepository customerRepository;
    private final JpaProviderRepository providerRepository;
    private final JpaUserStateRepository stateRepository;


    @Override
    public Users getCurrentUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Transactional
    @Override
    public CreateUserResponse createUser(CreateUserRequest userRequest) {

        Customers customers = new Customers("Member");
        Providers providers = new Providers("Provider");
        UserState userState = new UserState("활성");

        customerRepository.save(customers);
        providerRepository.save(providers);
        stateRepository.save(userState);

        Users users = Users.builder()
                        .customers(customers)
                        .userName(userRequest.getUserName())
                        .userBirth(userRequest.getUserBirth())
                        .userEmail(userRequest.getUserEmail())
                        .userPhone(userRequest.getUserPhone())
                        .userPassword(userRequest.getUserPassword())
                        .providers(providers)
                        .userState(userState)
                        .build();

        userRepository.save(users);
        log.info("User : {}", users);

        return CreateUserResponse.builder()
                    .userName(userRequest.getUserName())
                    .userBirth(userRequest.getUserBirth())
                    .userEmail(userRequest.getUserEmail())
                    .userPhone(userRequest.getUserPhone())
                    .userPassword(userRequest.getUserPassword())
                    .userConfirmPassword(userRequest.getUserConfirmPassword())
                    .build();
    }
}
