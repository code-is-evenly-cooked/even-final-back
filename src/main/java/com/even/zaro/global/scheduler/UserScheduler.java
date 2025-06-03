package com.even.zaro.global.scheduler;

import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserScheduler {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 30 3 * * *") // 매일 3시 30분
    @Transactional
    public void deleteWithdrawnUsers() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        List<User> users = userRepository.findByStatusAndDeletedAtBefore(Status.DELETED, threshold);
        userRepository.deleteAll(users);
    }
}
