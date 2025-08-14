package org.example.jjava_main.domain.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaderboardScheduler {

    private final LeaderboardService leaderboardService;

    /** 매 시 정각 스냅샷 (초 분 시 일 월 요일) */
    @Scheduled(cron = "0 0 * * * *")
    public void runHourlySnapshot() {
        int n = leaderboardService.snapshotOnce();
        log.info("Rank snapshot updated: {} rows (Top10)", n);
    }
}