package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.presentation.dto.request.CreateAuthorRequest;
import com.yes255.yes255booksusersserver.presentation.dto.response.AuthorResponse;

public interface AuthorService {

    AuthorResponse getAuthor(Long authorId);
    AuthorResponse getAuthorByName(String authorName);
    AuthorResponse createAuthor(CreateAuthorRequest request);
    void removeAuthor(Long authorId);
    boolean isExistAuthorByName(String authorName);
}
