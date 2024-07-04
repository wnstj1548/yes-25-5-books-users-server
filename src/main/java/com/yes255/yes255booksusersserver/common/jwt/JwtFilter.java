package com.yes255.yes255booksusersserver.common.jwt;

import com.yes255.yes255booksusersserver.application.service.CustomerService;
import com.yes255.yes255booksusersserver.common.exception.JwtException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.infrastructure.adaptor.AuthAdaptor;
import com.yes255.yes255booksusersserver.presentation.dto.request.customer.CustomerRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.customer.CustomerResponse;
import com.yes255.yes255booksusersserver.presentation.dto.response.customer.NoneMemberLoginResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
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
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
@Component
public class JwtFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;
    private final CustomerService customerService;
    private final AuthAdaptor authAdaptor;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getServletPath();

        if ("/users".equals(path) || "/users/sign-up".equals(path) ||
            "/users/find/email".equals(path) || "/user/find/password".equals(path)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (path.startsWith("/books") && StringUtils.isEmpty(request.getHeader("Authorization"))) {
            filterChain.doFilter(servletRequest, servletResponse);
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

            CustomerResponse customerResponse = customerService.createCustomer(new CustomerRequest("NONE_MEMBER"));
            NoneMemberLoginResponse noneMemberLoginResponse = authAdaptor.loginNoneMember(customerResponse);

            JwtUserDetails jwtUserDetails = JwtUserDetails.of(noneMemberLoginResponse.customerId(),
                noneMemberLoginResponse.role(), noneMemberLoginResponse.accessToken(), noneMemberLoginResponse.refreshToken());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                jwtUserDetails, null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + noneMemberLoginResponse.role()))
            );

            response.setHeader("Authorization", "Bearer " + noneMemberLoginResponse.accessToken());
            response.setHeader("Refresh-Token", noneMemberLoginResponse.refreshToken());

            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String token = getToken((HttpServletRequest) servletRequest);

        if (jwtProvider.isValidToken(token)) {
            Long userName = jwtProvider.getUserNameFromToken(token);
            String role = jwtProvider.getRolesFromToken(token);

            JwtUserDetails jwtUserDetails = JwtUserDetails.of(userName, role, token);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                jwtUserDetails, null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(servletRequest, servletResponse);
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