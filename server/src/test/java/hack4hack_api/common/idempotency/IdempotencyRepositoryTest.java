package hack4hack_api.common.idempotency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
public class IdempotencyRepositoryTest {
    private static final UUID ALICE = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    private IdempotencyRepository repository;

    @Test
    void secondClaimWithSameKeyIsRejected() {
        assertTrue(repository.claim(ALICE, "k1", "test", "a".repeat(64)));
        assertFalse(repository.claim(ALICE, "k1", "test", "a".repeat(64)));
    }

    @Test
    void findAfterClaimReturnsEmptyResult() {
        repository.claim(ALICE, "k1", "test", "a".repeat(64));

        Idempotency stored = repository.find(ALICE, "k1").orElseThrow();

        assertEquals("a".repeat(64), stored.requestHash());
        assertNull(stored.responseStatus());
        assertNull(stored.responseBody());
    }

    @Test
    void findOnUnknownKeyReturnsEmpty() {
        Optional<Idempotency> result = repository.find(ALICE, "never-used");

        assertTrue(result.isEmpty());
    }
    
    @Test
    void saveResultStoresStatusAndBody() {
        repository.claim(ALICE, "k1", "test", "a".repeat(64));

        repository.saveResult(ALICE, "k1", 201, "{\"id\":\"x\"}");

        Idempotency stored = repository.find(ALICE, "k1").orElseThrow();
        assertEquals(Integer.valueOf(201), stored.responseStatus());
        assertTrue(stored.responseBody().contains("\"id\""));
    }

    @Test
    void saveResultOnUnclaimedKeyFails() {
        assertThrows(IllegalStateException.class,
                () -> repository.saveResult(ALICE, "never-claimed", 201, "{}"));
    }
}
