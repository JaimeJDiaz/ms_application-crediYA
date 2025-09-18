package co.com.pragma.model.mail;

import reactor.core.publisher.Mono;

public interface MailService {
    Mono<Void> sendMail(String to, String subject, String body);
}

