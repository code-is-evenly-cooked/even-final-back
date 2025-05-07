package com.evenly.jachui.healthcheck;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MockRepository extends JpaRepository<MockEntity, Long> {
}
