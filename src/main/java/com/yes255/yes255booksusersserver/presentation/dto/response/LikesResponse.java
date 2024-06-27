package com.yes255.yes255booksusersserver.presentation.dto.response;

import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.Likes;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import lombok.Builder;

@Builder
public record LikesResponse (Long likesId, Long bookId, Long userId, boolean likesStatus)
{
    public static LikesResponse fromEntity(Likes likes) {
        return LikesResponse.builder()
                .likesId(likes.getLikesId())
                .userId(likes.getUser().getUserId())
                .bookId(likes.getBook().getBookId())
                .likesStatus(likes.isLikesStatus())
                .build();
    }
}
