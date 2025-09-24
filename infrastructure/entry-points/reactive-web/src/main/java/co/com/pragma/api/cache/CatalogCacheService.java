package co.com.pragma.api.cache;

import co.com.pragma.model.application.LoanType;
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
    private final Map<Long, LoanType> loanTypeIdToObject = new ConcurrentHashMap<>();

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
                .doOnNext(loanType -> loanTypeIdToObject.put(loanType.getId(), loanType))
                .then();
        return Mono.when(statusMono, loanTypeMono)
                .doOnSuccess(v -> log.info("Catálogos refrescados en caché"));
    }

    public Long getStatusIdByName(String name) {
        return statusNameToId.get(name);
    }

    @Override
    public Long getLoanTypeIdByName(String name) {
        return loanTypeIdToObject.values().stream()
                .filter(loanType -> loanType.getName().equalsIgnoreCase(name))
                .map(LoanType::getId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Mono<LoanType> getLoanTypeById(Long id) {
        LoanType cached = loanTypeIdToObject.get(id);
        if (cached != null) {
            return Mono.just(cached);
        } else {
            return loanTypeRepository.findById(id)
                .doOnNext(loanType -> loanTypeIdToObject.put(loanType.getId(), loanType));
        }
    }
}
