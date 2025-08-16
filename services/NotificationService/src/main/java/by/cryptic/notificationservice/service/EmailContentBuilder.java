package by.cryptic.notificationservice.service;

import by.cryptic.utils.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailContentBuilder {

    private final TemplateEngine templateEngine;

    public String buildOrderEmailContent(UUID orderId, OrderStatus orderStatus) {
        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("orderStatus", orderStatus);
        return templateEngine.process("order_status_email", context);
    }
}
