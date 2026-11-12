package hack4hack_api.common.outbox;

import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OutboxRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public OutboxRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void insert(String eventType, String aggregateType, UUID aggregateId,
                       UUID hackathonId, String payloadJson) {
        jdbc.update(
                """
                INSERT INTO outbox_events
                    (event_type, aggregate_type, aggregate_id, hackathon_id, event_payload)
                VALUES
                    (:eventType, :aggregateType, :aggregateId, :hackathonId, CAST(:payload AS jsonb))
                """,
                new MapSqlParameterSource()
                        .addValue("eventType", eventType)
                        .addValue("aggregateType", aggregateType)
                        .addValue("aggregateId", aggregateId)
                        .addValue("hackathonId", hackathonId)
                        .addValue("payload", payloadJson));
    }
}