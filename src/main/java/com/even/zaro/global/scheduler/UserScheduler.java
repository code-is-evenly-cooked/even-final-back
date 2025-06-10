package com.even.zaro.global.scheduler;

import com.even.zaro.entity.DormancyNoticeLog;
import com.even.zaro.entity.Status;
import com.even.zaro.entity.User;
import com.even.zaro.repository.DormancyNoticeLogRepository;
import com.even.zaro.repository.UserRepository;
import com.even.zaro.service.EmailService;
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
    private final DormancyNoticeLogRepository dormancyNoticeLogRepository;
    private final EmailService emailService;

    // 휴면 처리, 탈퇴 처리
    @Scheduled(cron = "0 30 3 * * *")// 매일 3시 30분
    @Transactional
    public void handleDormantAndSoftDelete() {
        LocalDateTime now = LocalDateTime.now();
        // 6개월 이상 미접속 -> 휴면
        List<User> toDormant = userRepository.findByStatusAndLastLoginAtBefore(Status.ACTIVE, now.minusMonths(6));
        toDormant.forEach(user -> user.changeStatus(Status.DORMANT));
        // 휴면 1년 경과 -> 탈퇴 처리
        List<User> softDeleteUser = userRepository.findByStatusAndUpdatedAtBefore(Status.DORMANT, now.minusYears(1));
        softDeleteUser.forEach(User::softDeleted);
    }

    // 탈퇴 30일 경과 -> 회원 정보 영구 삭제
    @Scheduled(cron = "0 45 3 * * *") // 매일 3시 45분
    @Transactional
    public void deleteWithdrawnUsers() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        List<User> users = userRepository.findByStatusAndDeletedAtBefore(Status.DELETED, threshold);
        userRepository.deleteAll(users);
    }

    // 휴면 예정 메일 전송
    @Scheduled(cron = "0 0 14 * * *") // 매일 오후 2시
    @Transactional
    public void sendDormancyPendingEmail() {
        LocalDateTime now = LocalDateTime.now();

        List<User> users = userRepository.findDormancyNoticeTargetsNative(Status.ACTIVE, now.minusMonths(5));

        users.forEach(user -> {
            emailService.sendDormancyPendingEmail(user.getEmail());
            dormancyNoticeLogRepository.save(new DormancyNoticeLog(user.getId(), user.getLastLoginAt().toLocalDate()));
        });
    }
}
