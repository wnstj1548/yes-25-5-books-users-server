package com.yes255.yes255booksusersserver.common.interceptor;


import com.yes255.yes255booksusersserver.common.jwt.JwtUserDetails;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationRequestInterceptor implements RequestInterceptor {

    @Value("${app.mode}")
    private String mode;

    @Override
    public void apply(RequestTemplate template) {

        if ("development".equals(mode)) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserDetails userDetails) {
            template.header("Authorization", "Bearer " + userDetails.accessToken());
        }
    }

}