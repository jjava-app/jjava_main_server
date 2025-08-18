package org.example.jjava_main.domain.leaderboard;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main.dto.LeaderboardResponse;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class LeaderboardRepository {

    private final EntityManager em;

    /** 1) scoreboard_tb에 '없는 유저만' 삽입 (base_score = 현재 점수) */
    public int initMissingScoreboards() {
        String sql = """
            INSERT INTO scoreboard_tb (user_id, base_score, updated_at)
            SELECT u.id, u.score, CURRENT_TIMESTAMP()
            FROM user_tb u
            WHERE NOT EXISTS (
                SELECT 1 FROM scoreboard_tb s WHERE s.user_id = u.id
            )
        """;
        return em.createNativeQuery(sql).executeUpdate();
    }

    /** 2) rank_tb 비우기 (가장 최근 스냅샷만 유지) */
    public int clearRankTable() {
        return em.createNativeQuery("DELETE FROM rank_tb").executeUpdate();
    }

    /** 3) Δ(= after - before) 기준 Top10을 rank_tb에 적재 (공동순위: DENSE_RANK) */
    public int insertTop10RanksFromDelta() {
        String sql = """
            INSERT INTO rank_tb (user_id, before_score, after_score, delta_score, rank_number)
            SELECT t.user_id,
                   t.before_score,
                   t.after_score,
                   t.delta_score,
                   t.rnk AS rank_number
            FROM (
                SELECT u.id AS user_id,
                       COALESCE(s.base_score, 0) AS before_score,
                       u.score AS after_score,
                       (u.score - COALESCE(s.base_score, 0)) AS delta_score,
                       DENSE_RANK() OVER (ORDER BY (u.score - COALESCE(s.base_score, 0)) DESC, u.id ASC) AS rnk
                FROM user_tb u
                LEFT JOIN scoreboard_tb s ON s.user_id = u.id
            ) t
            ORDER BY t.rnk, t.user_id
            LIMIT 10
        """;
        return em.createNativeQuery(sql).executeUpdate();
    }

    /** 4) 스냅샷 완료 후 scoreboard_tb 기준점을 '현재 점수'로 갱신 (다음 Δ 계산 대비) */
    public int updateScoreboardsToCurrent() {
        String sql = """
            MERGE INTO scoreboard_tb s
            USING (SELECT id AS user_id, score AS base_score FROM user_tb) u
            ON (s.user_id = u.user_id)
            WHEN MATCHED THEN
                UPDATE SET s.base_score = u.base_score, s.updated_at = CURRENT_TIMESTAMP()
            WHEN NOT MATCHED THEN
                INSERT (user_id, base_score, updated_at)
                VALUES (u.user_id, u.base_score, CURRENT_TIMESTAMP())
        """;
        return em.createNativeQuery(sql).executeUpdate();
    }

    /** (옵션) 단일 유저 점수 증가 */
    public int incrementUserScore(int userId, int delta) {
        String sql = "UPDATE user_tb SET score = score + ? WHERE id = ?";
        Query q = em.createNativeQuery(sql);
        q.setParameter(1, delta);
        q.setParameter(2, userId);
        return q.executeUpdate();
    }

    /** (옵션) 배치 점수 증가 */
    public int bulkIncrementUserScores(Map<Integer, Integer> deltas) {
        if (deltas == null || deltas.isEmpty()) return 0;
        String sql = "UPDATE user_tb SET score = score + ? WHERE id = ?";
        int sum = 0;
        for (var e : deltas.entrySet()) {
            Query q = em.createNativeQuery(sql);
            q.setParameter(1, e.getValue());
            q.setParameter(2, e.getKey());
            sum += q.executeUpdate();
        }
        return sum;
    }

    public LeaderboardResponse.DTO findTop10() {
        String sql = """
            SELECT t.user_id AS userId, t.username, t.score, t.rnk AS rank
            FROM (
                SELECT u.id AS user_id,
                       u.username,
                       u.score,
                       DENSE_RANK() OVER (ORDER BY u.score DESC, u.id ASC) AS rnk
                FROM user_tb u
            ) t
            ORDER BY t.rnk, t.user_id
            LIMIT 10
        """;

        Query query = em.createNativeQuery(sql);

        List<Object[]> rows = (List<Object[]>) query.getResultList();

        List<LeaderboardResponse.ItemDTO> items = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            // 컬럼 순서: userId(0), username(1), score(2), rank(3)
            items.add(LeaderboardResponse.ItemDTO.builder()
                    .userId(toInt(r[0]))
                    .username(r[1] != null ? r[1].toString() : null)
                    .score(toInt(r[2]))
                    .rank(toInt(r[3]))
                    .build());
        }

        return LeaderboardResponse.DTO.builder()
                .rankingList(items)
                .build();
    }

    // 숫자 타입 안전 캐스팅 헬퍼 (H2/DB 드라이버에 따라 BigInteger/BigDecimal로 오는 경우 대비)
    private static Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Integer i) return i;
        if (o instanceof Long l) return Math.toIntExact(l);
        if (o instanceof BigInteger bi) return bi.intValue();
        if (o instanceof BigDecimal bd) return bd.intValue();
        if (o instanceof Number n) return n.intValue();
        return Integer.valueOf(o.toString());
    }
}
