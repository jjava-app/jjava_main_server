package org.example.jjava_main.domain.leaderboard;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.jjava_main.dto.LeaderboardResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class LeaderboardService {
    private final LeaderboardRepository leaderboardRepository;

    /** 스냅샷 1회 실행: init → clear → insertTop10 → baseline 갱신 */
    @Transactional
    public int snapshotOnce() {
        leaderboardRepository.initMissingScoreboards();
        leaderboardRepository.clearRankTable();
        int inserted = leaderboardRepository.insertTop10RanksFromDelta();
        leaderboardRepository.updateScoreboardsToCurrent();
        return inserted; // 최대 10
    }

    /** (옵션) 단일 유저 점수 증가 + 즉시 스냅샷 */
    @Transactional
    public int incrementAndSnapshot(int userId, int delta) {
        leaderboardRepository.incrementUserScore(userId, delta);
        return snapshotOnce();
    }

    /** (옵션) 배치 점수 증가 + 즉시 스냅샷 */
    @Transactional
    public int bulkIncrementAndSnapshot(Map<Integer, Integer> deltas) {
        leaderboardRepository.bulkIncrementUserScores(deltas);
        return snapshotOnce();
    }

    public LeaderboardResponse.DTO top10List() {
        return leaderboardRepository.findTop10();
    }
}
