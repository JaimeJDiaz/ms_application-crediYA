package co.com.pragma.model.application.gateways;

public interface CatalogCachePort {
    Long getStatusIdByName(String name);
    Long getLoanTypeIdByName(String name);
}

