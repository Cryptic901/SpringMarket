package by.cryptic.analyticservice.controller;

import by.cryptic.analyticservice.dto.AnalyticDashboard;
import by.cryptic.analyticservice.listener.AnalyticListener;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminAnalyticController {

    @GetMapping("/dashboard")
    public ResponseEntity<AnalyticDashboard> getDashboard() {
        return ResponseEntity.ok(AnalyticDashboard.builder()
                .todayOrders(AnalyticListener.todayOrders)
                .activeUsers(AnalyticListener.activeUsers)
                .todayRevenue(AnalyticListener.todayRevenue)
                .build());
    }

}
