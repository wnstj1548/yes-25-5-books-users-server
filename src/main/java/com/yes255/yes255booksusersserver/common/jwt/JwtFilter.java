package com.yes255.yes255booksusersserver.common.jwt;

import com.yes255.yes255booksusersserver.application.service.CustomerService;
import com.yes255.yes255booksusersserver.common.exception.JwtException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.infrastructure.adaptor.AuthAdaptor;
import com.yes255.yes255booksusersserver.presentation.dto.request.customer.CustomerRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.customer.CustomerResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.customer.NoneMemberLoginResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.user.JwtAuthResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomerService customerService;
    private final AuthAdaptor authAdaptor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/users".equals(path) || "/users/sign-up".equals(path) ||
            "/users/find/email".equals(path) || "/user/find/password".equals(path) ||
            "/users/check-email".equals(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        if ((path.startsWith("/books") || path.startsWith("/reviews/books")) && StringUtils.isEmpty(request.getHeader("Authorization"))) {
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getMethod().equalsIgnoreCase("POST") && path.startsWith("/users/cart-books")
            && StringUtils.isEmpty(request.getHeader("Authorization"))) {

            /*
             * 1. 인증 서버에 회원가입 요청 (/auth/login/none)
             * 2. 엑세스 토큰 받음
             * 3. 인증 서버로 토큰 해석 요청 (정보 받음)
             * 4. JwtUserDetails 등록
             */

            CustomerResponse customerResponse = customerService.createCustomer(
                new CustomerRequest("NONE_MEMBER"));
            NoneMemberLoginResponse noneMemberLoginResponse = authAdaptor.loginNoneMember(
                customerResponse);

            JwtUserDetails jwtUserDetails = JwtUserDetails.of(noneMemberLoginResponse.customerId(),
                noneMemberLoginResponse.role(), noneMemberLoginResponse.accessToken(),
                noneMemberLoginResponse.refreshToken());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                jwtUserDetails, null,
                Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + noneMemberLoginResponse.role()))
            );

            response.setHeader("Authorization", "Bearer " + noneMemberLoginResponse.accessToken());
            response.setHeader("Refresh-Token", noneMemberLoginResponse.refreshToken());

            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(request);
        String uuid = jwtProvider.getUserNameFromToken(token);
        JwtAuthResponse jwtAuthResponse = authAdaptor.getUserInfoByUUID(uuid);

        JwtUserDetails jwtUserDetails = JwtUserDetails.of(jwtAuthResponse.customerId(),
            jwtAuthResponse.role(), token);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            jwtUserDetails, null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + jwtAuthResponse.role()))
        );

        response.setHeader("Refresh-Token", jwtAuthResponse.refreshJwt());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        throw new JwtException(
            ErrorStatus.toErrorStatus("헤더에서 토큰을 찾을 수 없습니다.", 401, LocalDateTime.now())
        );
    }
}