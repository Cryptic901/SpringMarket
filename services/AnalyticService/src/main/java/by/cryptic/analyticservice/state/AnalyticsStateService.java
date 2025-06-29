package by.cryptic.analyticservice.state;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class AnalyticsStateService {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "analytics:";

    public void saveState(int activeUsers, BigDecimal todayRevenue, int todayOrders) {
        redisTemplate.opsForValue().set(PREFIX + "activeUsers", String.valueOf(activeUsers));
        redisTemplate.opsForValue().set(PREFIX + "todayRevenue", todayRevenue.toString());
        redisTemplate.opsForValue().set(PREFIX + "todayOrders", String.valueOf(todayOrders));
    }

    public int loadActiveUsers() {
        String value = redisTemplate.opsForValue().get(PREFIX + "activeUsers");
        return value == null ? 0 : Integer.parseInt(value);
    }

    public BigDecimal loadTodayRevenue() {
        String value = redisTemplate.opsForValue().get(PREFIX + "todayRevenue");
        return value == null ? BigDecimal.ZERO : new BigDecimal(value);
    }

    public int loadTodayOrders() {
        String value = redisTemplate.opsForValue().get(PREFIX + "todayOrders");
        return value == null ? 0 : Integer.parseInt(value);
    }

    public void saveStateToArchive(String dateKey, AtomicInteger activeUsers,
                                   AtomicReference<BigDecimal> todayRevenue, AtomicInteger todayOrders) {
        String key = "analytics:archive:" + dateKey + ":";
        redisTemplate.opsForValue().set(key + "users", String.valueOf(activeUsers));
        redisTemplate.opsForValue().set(key + "revenue", todayRevenue.toString());
        redisTemplate.opsForValue().set(key + "orders", String.valueOf(todayOrders));
    }
}
