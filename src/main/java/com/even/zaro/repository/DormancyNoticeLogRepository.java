package com.even.zaro.repository;

import com.even.zaro.entity.DormancyNoticeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DormancyNoticeLogRepository extends JpaRepository<DormancyNoticeLog, Long> {
}
