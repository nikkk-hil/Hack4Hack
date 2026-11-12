package hack4hack_api.common.audit;

import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuditRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public AuditRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void insert(UUID actorUserId, UUID teamId, UUID hackathonId,
                       String action, String metadataJson) {
        jdbc.update(
                """
                INSERT INTO audit_logs (actor_user_id, team_id, hackathon_id, action, metadata)
                VALUES (:actorUserId, :teamId, :hackathonId, :action, CAST(:metadata AS jsonb))
                """,
                new MapSqlParameterSource()
                        .addValue("actorUserId", actorUserId)
                        .addValue("teamId", teamId)
                        .addValue("hackathonId", hackathonId)
                        .addValue("action", action)
                        .addValue("metadata", metadataJson));
    }
}