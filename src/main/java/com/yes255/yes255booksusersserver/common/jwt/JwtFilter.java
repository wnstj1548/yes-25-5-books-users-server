//package com.yes255.yes255booksusersserver.common.jwt;
//
//import com.yes255.yes255booksusersserver.common.exception.JwtException;
//import com.yes255.yes255booksusersserver.common.exception.payload.ErrorStatus;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.GenericFilterBean;
//
//@RequiredArgsConstructor
//@Component
//public class JwtFilter extends GenericFilterBean {
//    private final JwtProvider jwtProvider;
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
//        FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        String path = request.getServletPath();
//
//        if ("/users".equals(path)) {
//            filterChain.doFilter(servletRequest, servletResponse);
//            return;
//        }
//
//        String token = getToken((HttpServletRequest) servletRequest);
//
//        if (jwtProvider.isValidToken(token)) {
//            Long userName = jwtProvider.getUserNameFromToken(token);
//            String role = jwtProvider.getRolesFromToken(token);
//
//            JwtUserDetails jwtUserDetails = JwtUserDetails.of(userName, role, token);
//
//            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                jwtUserDetails, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
//            );
//
//            SecurityContextHolder.getContext().setAuthentication(authToken);
//        }
//
//        filterChain.doFilter(servletRequest, servletResponse);
//    }
//
//    private String getToken(HttpServletRequest request) {
//        String bearer = request.getHeader("Authorization");
//        if (bearer != null && bearer.startsWith("Bearer ")) {
//            return bearer.substring(7);
//        }
//
//        throw new JwtException(
//            ErrorStatus.toErrorStatus("헤더에서 토큰을 찾을 수 없습니다.", 401, LocalDateTime.now())
//        );
//    }
//}