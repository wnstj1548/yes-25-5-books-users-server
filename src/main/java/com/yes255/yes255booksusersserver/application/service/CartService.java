package com.yes255.yes255booksusersserver.application.service;

public interface CartService {




    // todo : n개월이 지나면 회원 등급 하락 구현
    // todo : 휴면 전환 기능 추가
    // todo : 회원 탈퇴 기능 외래키 제약 해결
    // todo : 예외 처리
    // todo : userState CRUD 구현 및 테스트

    void deleteByUserId(Long cartId);
}
