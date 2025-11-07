package by.cryptic.paymentservice.service;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.exceptions.PaymentTooManyRequestException;
import by.cryptic.paymentservice.model.write.Payment;
import by.cryptic.paymentservice.repository.write.PaymentRepository;
import by.cryptic.utils.enums.PaymentMethod;
import by.cryptic.utils.enums.PaymentStatus;
import by.cryptic.utils.event.payment.PaymentCreatedEvent;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/* Change to real Payment API */
@Service
@Slf4j
public class ExternalPaymentService {

    private final PaymentRepository paymentRepository;

    private final String stripeSecretKey;

    private final StripeClient stripeClient;

    public ExternalPaymentService(PaymentRepository paymentRepository,
                                  @Value("${stripe.secret.key}") String stripeSecretKey) {
        this.paymentRepository = paymentRepository;
        this.stripeSecretKey = stripeSecretKey;
        this.stripeClient = new StripeClient(stripeSecretKey);
    }


    @RateLimiter(name = "paymentRatelimiter", fallbackMethod = "paymentCreateRateLimiterFallback")
    public Payment paymentWithExternalAPI(PaymentCreatedEvent command) {
        try {
            PaymentIntent paymentIntent = createStripePaymentIntent(command);
            Payment payment = Payment.builder()
                    .paymentMethod(command.getPaymentMethod())
                    .paymentStatus(mapStripePaymentStatus(paymentIntent.getStatus()))
                    .timestamp(LocalDateTime.now())
                    .price(command.getPrice())
                    .orderId(command.getOrderId())
                    .userId(command.getUserId())
                    .externalPaymentId(paymentIntent.getId())
                    .build();
            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment created successfully. ID: {}, Stripe ID: {}", savedPayment.getId(), paymentIntent.getId());
            return savedPayment;
        } catch (StripeException e) {
            if (e.getStatusCode() == 429) {
                log.warn("Rate limit exceeded for Stripe API! Consider lowering request rate.");
                throw new PaymentTooManyRequestException("Rate limit exceeded for Stripe API!");
            }
            log.error("Stripe API Error for order {}: {}", command.getOrderId(), e.getMessage(), e);
            throw new CreatingException("Failed to create payment via Stripe:" + e.getMessage(), e);
        }
    }

    private PaymentIntent createStripePaymentIntent(PaymentCreatedEvent command) throws StripeException {
        long amountInCents = command.getPrice()
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .setDescription("Order #" + command.getOrderId())
                .putMetadata("orderId", command.getOrderId().toString())
                .putMetadata("userId", command.getUserId().toString())
                .setPaymentMethod(mapStripePaymentMethods(command.getPaymentMethod())) // <-- delete when integrate with frontend
                .setConfirm(true) // <-- set to false when integrate with frontend
                .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                        .build())
                .build();
        RequestOptions requestOptions = RequestOptions.builder()
                .setIdempotencyKey("order_" + command.getOrderId())
                .build();
        return stripeClient.paymentIntents().create(params, requestOptions);
    }

    public PaymentStatus mapStripePaymentStatus(String stripePaymentStatus) {
        return switch (stripePaymentStatus) {
            case "succeeded" -> PaymentStatus.SUCCESS;
            case "canceled" -> PaymentStatus.CANCELED;
            case "requires_payment_method" -> PaymentStatus.FAILED;
            default -> PaymentStatus.PENDING;
        };
    }

    public String mapStripePaymentMethods(PaymentMethod paymentMethod) {
        return switch (paymentMethod) {
            case CARD -> "pm_card_visa"; // тестовая карта
            case SEPA -> "sepa_debit";
            case BANK_TRANSFER -> "us_bank_account";
            default -> null; // остальные (PayPal, Apple Pay, Google Pay) через AutomaticPaymentMethods
        };
    }

    public Payment paymentCreateRateLimiterFallback(PaymentCreatedEvent paymentCreatedEvent, Throwable t) {
        log.error("Failed to create {} because request exceed rate limiter to external API. Cause: {}", paymentCreatedEvent.toString(), t.getMessage(), t);
        throw new CreatingException("Failed to create order:" + paymentCreatedEvent, t);
    }
}
