package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Providers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProviderRepository extends JpaRepository<Providers, Long> {
}
