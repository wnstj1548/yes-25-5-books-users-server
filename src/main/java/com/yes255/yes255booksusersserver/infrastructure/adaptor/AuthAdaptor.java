package com.yes255.yes255booksusersserver.infrastructure.adaptor;


import com.yes255.yes255booksusersserver.common.config.FeignClientConfig;
import com.yes255.yes255booksusersserver.presentation.dto.response.customer.CustomerResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.customer.NoneMemberLoginResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.JwtAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "authAdaptor", url = "${api.auth}/auth", configuration = FeignClientConfig.class)
public interface AuthAdaptor {

    @PostMapping("/login/none")
    NoneMemberLoginResponse loginNoneMember(@RequestBody CustomerResponse response);

    @GetMapping("/info")
    JwtAuthResponse getUserInfoByUUID(@RequestParam String uuid);
}
