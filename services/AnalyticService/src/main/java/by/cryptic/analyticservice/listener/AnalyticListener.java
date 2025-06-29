package by.cryptic.analyticservice.listener;

import by.cryptic.analyticservice.state.AnalyticsStateService;
import by.cryptic.utils.event.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Service
@Slf4j
public class AnalyticListener implements SmartLifecycle {

    public static final AtomicInteger activeUsers = new AtomicInteger(0);
    public static final AtomicReference<BigDecimal> todayRevenue = new AtomicReference<>(BigDecimal.ZERO);
    public static final AtomicInteger todayOrders = new AtomicInteger(0);
    private final ObjectMapper objectMapper;
    private final AnalyticsStateService analyticsStateService;
    private volatile boolean running = false;


    @EventListener(ApplicationReadyEvent.class)
    public void restoreState() {
        activeUsers.set(analyticsStateService.loadActiveUsers());
        todayRevenue.set(analyticsStateService.loadTodayRevenue());
        todayOrders.set(analyticsStateService.loadTodayOrders());
        log.info("Restored analytics state from Redis {}, {}, {}", activeUsers, todayRevenue, todayOrders);
    }

    @Scheduled(cron = "@daily")
    public void daily() {
        int users = activeUsers.get();
        int orders = todayOrders.get();
        BigDecimal revenue = todayRevenue.get();

        log.info("Daily statistics: Revenue - {}, Orders - {}, Users - {}", revenue, orders, users);
        String dateKey = LocalDateTime.now().minusDays(1).toString();
        analyticsStateService.saveStateToArchive(dateKey, activeUsers, todayRevenue, todayOrders);

        activeUsers.set(0);
        todayOrders.set(0);
        todayRevenue.set(BigDecimal.ZERO);
    }

    @KafkaListener(topics = {"user-topic", "order-topic"}, groupId = "analytics-group")
    public void listenUserRegister(String rawEvent) throws JsonProcessingException {
        log.info("Received user created event and trying to increase the value: {}", rawEvent);
        JsonNode node = objectMapper.readTree(rawEvent);
        EventType eventType = EventType.valueOf(node.get("eventType").asText());
        switch (eventType) {
            case UserLoginedEvent -> activeUsers.incrementAndGet();
            case OrderCreatedEvent -> {
                BigDecimal totalAmount = BigDecimal.valueOf(node.get("price").asDouble());
                todayRevenue.updateAndGet(current -> (current.add(totalAmount)));
                todayOrders.incrementAndGet();
            }
        }
    }

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        analyticsStateService.saveState(activeUsers.get(), todayRevenue.get(), todayOrders.get());
        log.info("Saved analytics state to Redis {}, {}, {}", activeUsers.get(), todayRevenue.get(), todayOrders.get());
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
