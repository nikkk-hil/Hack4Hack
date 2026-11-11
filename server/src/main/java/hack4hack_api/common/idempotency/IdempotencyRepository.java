package hack4hack_api.common.idempotency;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository 
public class IdempotencyRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public IdempotencyRepository(NamedParameterJdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    public boolean claim(UUID userId, String idempotencyKey, String operation, String requestHash){
        int inserted = jdbc.update(
            """
            INSERT INTO idempotency_keys (user_id, idempotency_key, operation, request_hash)
            VALUES (:userId, :idempotencyKey, :operation, :requestHash)
            ON CONFLICT (user_id, idempotency_key) DO NOTHING;
            """,
            new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("idempotencyKey", idempotencyKey)
                .addValue("operation", operation)
                .addValue("requestHash", requestHash)
        );
        
        return inserted == 1;
        
    }

    public Optional<Idempotency> find(UUID userId, String idempotencyKey){
        List<Idempotency> rows = jdbc.query(
            """
            SELECT request_hash, response_status, response_body
            FROM idempotency_keys WHERE user_id = :userId AND idempotency_key = :idempotencyKey;
            """, 
            new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("idempotencyKey", idempotencyKey),
            (rs, rowNum) -> new Idempotency(
                rs.getString("request_hash"),
                rs.getObject("response_status", Integer.class),
                rs.getString("response_body")
            )
        );

        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    public void saveResult(UUID userId, String idempotencyKey, int status, String payloadJson) {
        int updated = jdbc.update(
            """
            UPDATE idempotency_keys
            SET response_status = :status,
                response_body = CAST(:payload AS jsonb),
                completed_at = NOW()
            WHERE user_id = :userId AND idempotency_key = :key
            """,
            new MapSqlParameterSource()
                    .addValue("status", status)
                    .addValue("payload", payloadJson)
                    .addValue("userId", userId)
                    .addValue("key", idempotencyKey));

        if (updated != 1) {
            throw new IllegalStateException(
                    "Expected to save the result for exactly one idempotency key, but updated " + updated);
        }
    }    
}
