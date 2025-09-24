package co.com.pragma.model.application.gateways;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.User;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface QueueSender {
    Mono<Void> sendApplicationStatusChange(Application application, String status, String email);

    Mono<Void> sendApplicationForAutomaticValidation(Application savedApp, List<Application> approvedApps, User user, Map<Long, BigDecimal> loanTypeRateMap);
}
