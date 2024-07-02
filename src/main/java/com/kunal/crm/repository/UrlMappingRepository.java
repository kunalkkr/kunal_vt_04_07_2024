package com.kunal.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kunal.crm.entity.UrlMapping;

import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {

    Optional<UrlMapping> findByShortCode(String shortCode);
}
