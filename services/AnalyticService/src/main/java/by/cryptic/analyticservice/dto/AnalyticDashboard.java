package by.cryptic.analyticservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Data
@Builder
public class AnalyticDashboard {
    private AtomicInteger todayOrders;
    private AtomicInteger activeUsers;
    private AtomicReference<BigDecimal> todayRevenue;
}
