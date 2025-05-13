package com.even.zaro.healthcheck;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MockRepository extends JpaRepository<MockEntity, Long> {
}
