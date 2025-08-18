package org.example.jjava_main.domain.leaderboard;

import jakarta.persistence.*;
import lombok.*;
import org.example.jjava_main.domain.user.User;

@Data
@NoArgsConstructor
@Entity
@Table(name = "rank_tb")
public class Rank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private Integer beforeScore; // 이전 점수
    private Integer afterScore; // 갱신된 점수
    private Integer deltaScore; // 점수 차

    private Integer rankNumber; // 약어 회피를 위한 풀네임

    @Builder
    public Rank(Integer id, User user, Integer beforeScore, Integer afterScore, Integer deltaScore, Integer rankNumber) {
        this.id = id;
        this.user = user;
        this.beforeScore = beforeScore;
        this.afterScore = afterScore;
        this.deltaScore = deltaScore;
        this.rankNumber = rankNumber;
    }
}
