package com.yes255.yes255booksusersserver.application.service;

public interface CartService {

    void deleteByUserId(Long cartId);
}


    // todo : n개월이 지나면 회원 등급 하락 구현
    // todo : 휴면 전환 기능 추가
