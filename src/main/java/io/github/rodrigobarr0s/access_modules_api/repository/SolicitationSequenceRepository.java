package io.github.rodrigobarr0s.access_modules_api.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class SolicitationSequenceRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Long getNextSequenceValue() {
        return ((Number) entityManager
                .createNativeQuery("SELECT NEXTVAL('solicitation_seq')")
                .getSingleResult()).longValue();
    }
}
