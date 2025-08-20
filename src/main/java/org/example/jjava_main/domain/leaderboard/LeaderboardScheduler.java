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
//    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
//    public void runHourlySnapshot() {
//        int n = leaderboardService.snapshotOnce();
//        log.info("Rank snapshot updated: {} rows (Top10)", n);
//    }

    /** 매일 23:30 스냅샷: 현재 점수 기준 Δ로 rank_tb에 배치 적재(Top10) + 기준점 갱신 */
    @Scheduled(cron = "0 30 23 * * *", zone = "Asia/Seoul")
    public void runDailySnapshot2330() {
        int n = leaderboardService.snapshotOnce();
        log.info("Rank snapshot (daily 23:30) saved: {} rows (Top10)", n);
    }

    /** 주간 리셋: 매주 월요일 00:00에 기준점(base_score) = 현재 점수로 초기화 + rank_tb 비우기 */
    @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul")
    public void resetWeeklyBaseline() {
        int affected = leaderboardService.resetForNewWeek();
        log.info("Leaderboard weekly reset done. Baseline updated for {} users, rank table cleared.", affected);
    }
}