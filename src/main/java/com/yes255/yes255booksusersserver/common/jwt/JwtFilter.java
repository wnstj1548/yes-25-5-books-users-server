package com.yes255.yes255booksusersserver.common.jwt;

import com.yes255.yes255booksusersserver.application.service.CustomerService;
import com.yes255.yes255booksusersserver.common.exception.JwtException;
import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
import com.yes255.yes255booksusersserver.presentation.dto.response.LoginUserResponse;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();

        //토큰 필요없는 거
        if ("/users".equals(path) || "/users/sign-up".equals(path) ||
                "/users/find/email".equals(path) || "/user/find/password".equals(path) ||
                "/users/check-email".equals(path) || path.startsWith("/books/search") ||
                 path.startsWith("/books/categories") || path.startsWith("/books/category")
                || path.startsWith("/books/books/category")
                || "/users/dormant".equals(path) || "/users/find-email".equals(path)
                || path.matches("/books/likes/book/\\d") || path.startsWith("/users/cart-books")) {
            filterChain.doFilter(request, response);
            return;
        }

        //토큰이 있으면 받고 없으면 바로 리턴
        if (path.matches("/books/likes/\\d+/exist") || path.matches("/users/coupons/claim")) {
            try {
                String token = getAccessToken(request);
                LoginUserResponse user = jwtProvider.getLoginUserFromToken(token);

                JwtUserDetails jwtUserDetails = JwtUserDetails.of(user.userId(),
                        user.userRole(), token);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        jwtUserDetails, null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.userRole()))
                );

                response.setHeader("Authorization", "Bearer " + token);
                response.setHeader("Refresh-Token", request.getHeader("Refresh-Token"));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);
                return;

            } catch (Exception e) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        if (((!path.startsWith("/books/likes") && path.startsWith("/books")) || (path.startsWith("/reviews/books")) && StringUtils.isEmpty(request.getHeader("Authorization")))) {
            filterChain.doFilter(request, response);
            return;
        }

        if(!request.getMethod().equalsIgnoreCase("POST") && path.startsWith("/books/likes/books")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getAccessToken(request);
        LoginUserResponse user = jwtProvider.getLoginUserFromToken(token);

        JwtUserDetails jwtUserDetails = JwtUserDetails.of(user.userId(),
                user.userRole(), token);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                jwtUserDetails, null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.userRole()))
        );

        response.setHeader("Authorization", "Bearer " + token);
        response.setHeader("Refresh-Token", request.getHeader("Refresh-Token"));

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        throw new JwtException(
                ErrorStatus.toErrorStatus("헤더에서 토큰을 찾을 수 없습니다.", 401, LocalDateTime.now())
        );
    }
}