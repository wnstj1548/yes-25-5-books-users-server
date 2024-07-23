package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaTagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByLastModifiedAfter(LocalDateTime lastModified);

}
