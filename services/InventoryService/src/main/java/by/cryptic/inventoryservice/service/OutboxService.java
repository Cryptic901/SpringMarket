package by.cryptic.inventoryservice.service;

import by.cryptic.inventoryservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;

    @Scheduled(cron = "@hourly")
    @Transactional
    public void cleanup() {
        LocalDateTime time = LocalDateTime.now().minusHours(1);
        outboxRepository.deleteByCreatedAtBefore(time);
    }
}
