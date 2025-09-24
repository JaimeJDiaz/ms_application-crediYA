package co.com.pragma.model.application.gateways;

import co.com.pragma.model.application.Application;
import reactor.core.publisher.Mono;

import java.util.List;

public interface QueueSender {
    Mono<Void> sendApplicationStatusChange(Application application, String status, String email);

    Mono<Void> sendApplicationForAutomaticValidation(Application savedApp, List<Application> approvedApps);
}
