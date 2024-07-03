package com.yes255.yes255booksusersserver.application.service;

import com.yes255.yes255booksusersserver.persistance.domain.index.BookIndex;

import java.util.List;

public interface BookSearchService {

    List<BookIndex> searchBookByName(String bookName);

    List<BookIndex> searchBookByDescription(String description);

    List<BookIndex> searchBookByTagName(String tagName);

    List<BookIndex> searchBookByAuthorName(String authorName);
}
