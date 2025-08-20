package org.example.jjava_main.controller;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main._core.util.Resp;
import org.example.jjava_main.domain.leaderboard.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/leaderboard")
@RequiredArgsConstructor
@RestController
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    @PostMapping("/snapshot")
    public ResponseEntity<?> snapshot() {
        return Resp.ok(leaderboardService.snapshotOnce());
    }

    @PostMapping("/increment/{userId}/{delta}")
    public ResponseEntity<?> incrementAndSnapshot(@PathVariable int userId, @PathVariable int delta) {
        return Resp.ok(leaderboardService.incrementAndSnapshot(userId, delta));
    }

    @PostMapping("/bulk-increment")
    public ResponseEntity<?> bulkIncrementAndSnapshot(@RequestBody Map<Integer, Integer> deltas) {
        return Resp.ok(leaderboardService.bulkIncrementAndSnapshot(deltas));
    }

    @GetMapping("/top10")
    public ResponseEntity<?> top10List() {
        var respDTO = leaderboardService.top10List();
        return Resp.ok(respDTO);
    }
}
