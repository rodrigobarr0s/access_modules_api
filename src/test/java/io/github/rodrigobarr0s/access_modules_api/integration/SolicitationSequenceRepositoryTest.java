package io.github.rodrigobarr0s.access_modules_api.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.rodrigobarr0s.access_modules_api.repository.SolicitationSequenceRepository;

@SpringBootTest
@ActiveProfiles("test")
class SolicitationSequenceRepositoryTest {

    @Autowired
    private SolicitationSequenceRepository sequenceRepository;

    @Test
    @DisplayName("Deve retornar próximo valor da sequência solicitation_seq")
    void deveRetornarProximoValorDaSequencia() {
        Long v1 = sequenceRepository.getNextSequenceValue();
        Long v2 = sequenceRepository.getNextSequenceValue();

        assertNotNull(v1);
        assertNotNull(v2);
        assertTrue(v2 > v1);
    }
}

