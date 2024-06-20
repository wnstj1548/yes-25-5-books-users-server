package com.yes255.yes255booksusersserver.presentation.dto.response;

import com.yes255.yes255booksusersserver.persistance.domain.Book;
import com.yes255.yes255booksusersserver.persistance.domain.User;
import lombok.Builder;

@Builder
public record LikesResponse (Long likesId, Long bookId, Long userId, boolean likesStatus) {
}
