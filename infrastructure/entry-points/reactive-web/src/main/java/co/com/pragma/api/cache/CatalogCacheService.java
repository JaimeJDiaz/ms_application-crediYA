package co.com.pragma.api.cache;

import co.com.pragma.model.application.gateways.CatalogCachePort;
import co.com.pragma.model.application.gateways.LoanTypeRepository;
import co.com.pragma.model.application.gateways.StatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CatalogCacheService implements CatalogCachePort {
    private final StatusRepository statusRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final Map<String, Long> statusNameToId = new ConcurrentHashMap<>();
    private final Map<String, Long> loanTypeNameToId = new ConcurrentHashMap<>();

    public CatalogCacheService(StatusRepository statusRepository, LoanTypeRepository loanTypeRepository) {
        this.statusRepository = statusRepository;
        this.loanTypeRepository = loanTypeRepository;
    }

    @PostConstruct
    public void init() {
        refreshCache().subscribe();
    }

    public Mono<Void> refreshCache() {
        Mono<Void> statusMono = statusRepository.findAll()
                .doOnNext(status -> statusNameToId.put(status.getName(), status.getId()))
                .then();
        Mono<Void> loanTypeMono = loanTypeRepository.findAll()
                .doOnNext(loanType -> loanTypeNameToId.put(loanType.getName(), loanType.getId()))
                .then();
        return Mono.when(statusMono, loanTypeMono)
                .doOnSuccess(v -> log.info("Catálogos refrescados en caché"));
    }

    public Long getStatusIdByName(String name) {
        return statusNameToId.get(name);
    }

    public Long getLoanTypeIdByName(String name) {
        return loanTypeNameToId.get(name);
    }
}
