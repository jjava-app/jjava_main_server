package org.example.jjava_main.domain.leaderboard;

import jakarta.persistence.*;
import lombok.*;
import org.example.jjava_main.domain.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "scoreboard_tb")
public class Scoreboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(unique = true)
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    private Integer baseScore;

    @CreationTimestamp
    private Timestamp updatedAt;

    @Builder
    public Scoreboard(Integer id, User user, Integer baseScore, Timestamp updatedAt) {
        this.id = id;
        this.user = user;
        this.baseScore = baseScore;
        this.updatedAt = updatedAt;
    }

    public void updateTo(int newScore) {
        this.baseScore = newScore;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
