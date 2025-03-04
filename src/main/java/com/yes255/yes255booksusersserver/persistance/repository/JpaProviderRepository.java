package com.yes255.yes255booksusersserver.persistance.repository;

import com.yes255.yes255booksusersserver.persistance.domain.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProviderRepository extends JpaRepository<Provider, Long> {

    Provider findByProviderName(String providerName);
}
