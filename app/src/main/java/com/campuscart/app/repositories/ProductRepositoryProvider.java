package com.campuscart.app.repositories;

/**
 * Service Locator / Provider for ProductRepository.
 * Manages access to either the in-memory development repository
 * or the Cloud Firestore repository.
 */
public final class ProductRepositoryProvider {

    private static ProductRepository customRepository;

    private ProductRepositoryProvider() {}

    public static synchronized ProductRepository getRepository() {
        if (customRepository != null) {
            return customRepository;
        }
        return InMemoryProductRepository.getInstance();
    }

    public static synchronized void setRepository(ProductRepository repository) {
        customRepository = repository;
    }
}
