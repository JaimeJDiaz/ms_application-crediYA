package co.com.pragma.sqs.sender;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.User;
import co.com.pragma.model.application.gateways.QueueSender;
import co.com.pragma.sqs.sender.config.SQSSenderProperties;
import co.com.pragma.sqs.sender.exception.SqsSerializationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements QueueSender {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> sendApplicationStatusChange(Application application, String status, String email) {
        return Mono.fromCallable(() -> {
            var message = new java.util.HashMap<String, Object>();
            message.put("applicationId", application.getId());
            message.put("userId", application.getUserId());
            message.put("status", status);
            message.put("email", email);
            message.put("updatedAt", java.time.Instant.now().toString());
            try {
                return objectMapper.writeValueAsString(message);
            } catch (JsonProcessingException e) {
                throw new SqsSerializationException("Error serializando mensaje SQS", e);
            }
        })
        .flatMap(this::send)
        .then();
    }

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }

    @Override
    public Mono<Void> sendApplicationForAutomaticValidation(Application savedApp, List<Application> approvedApps, User user, Map<Long, BigDecimal> loanTypeRateMap) {
        return Mono.fromCallable(() -> {
            var message = new java.util.HashMap<String, Object>();
            message.put("application", savedApp);
            message.put("approvedApplications", approvedApps);
            message.put("user", user);
            message.put("sentAt", java.time.Instant.now().toString());
            message.put("interestRates", loanTypeRateMap);
            try {
                return objectMapper.writeValueAsString(message);
            } catch (JsonProcessingException e) {
                throw new SqsSerializationException("Error serializando mensaje SQS para validación automática", e);
            }
        })
        .flatMap(this::sendToAutomaticValidationQueue)
        .then();
    }

    private Mono<String> sendToAutomaticValidationQueue(String message) {
        return Mono.fromCallable(() -> buildAutomaticValidationRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Automatic validation message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildAutomaticValidationRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.automaticValidationQueueUrl())
                .messageBody(message)
                .build();
    }





}
