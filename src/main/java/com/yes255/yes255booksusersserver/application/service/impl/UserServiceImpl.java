package com.yes255.yes255booksusersserver.application.service.impl;

import com.yes255.yes255booksusersserver.application.service.UserService;
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
public class UserServiceImpl implements UserService {

    private final JpaUserRepository userRepository;

    private final JpaCustomerRepository customerRepository;
    private final JpaProviderRepository providerRepository;
    private final JpaUserStateRepository stateRepository;


    @Override
    public User getCurrentUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Transactional
    @Override
    public CreateUserResponse createUser(CreateUserRequest userRequest) {

        Customer customer = new Customer("Member");
        Provider provider = new Provider("Provider");
        UserState userState = new UserState("활성");

        customerRepository.save(customer);
        providerRepository.save(provider);
        stateRepository.save(userState);

        User user = User.builder()
                        .customer(customer)
                        .userName(userRequest.userName())
                        .userBirth(userRequest.userBirth())
                        .userEmail(userRequest.userEmail())
                        .userPhone(userRequest.userPhone())
                        .userPassword(userRequest.userPassword())
                        .provider(provider)
                        .userState(userState)
                        .build();

        userRepository.save(user);
        log.info("User : {}", user);

        return CreateUserResponse.builder()
                    .userName(userRequest.userName())
                    .userBirth(userRequest.userBirth())
                    .userEmail(userRequest.userEmail())
                    .userPhone(userRequest.userPhone())
                    .userPassword(userRequest.userPassword())
                    .userConfirmPassword(userRequest.userConfirmPassword())
                    .build();
    }
}
