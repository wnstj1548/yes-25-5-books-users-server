package com.yes255.yes255booksusersserver.persistance.domain.enumtype;

import lombok.Getter;

@Getter
public enum ReviewType {
    GENERAL(200, "리뷰 적립 - 일반"),
    IMAGE(500, "리뷰 적립 - 사진 첨부");

    private final Integer point;
    private final String logName;

    ReviewType(Integer point, String logName) {
        this.point = point;
        this.logName = logName;
    }
}
