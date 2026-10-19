package com.bookit.service.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findAllByOrderByCreatedAtDesc();
}
