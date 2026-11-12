package hack4hack_api.team;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TeamRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public TeamRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean isParticipant(UUID hackathonId, UUID userId) {
        Boolean result = jdbc.queryForObject(
                """
                        SELECT EXISTS(
                            SELECT 1 FROM hackathon_participants
                            WHERE hackathon_id = :hackathonId AND
                            user_id = :userId
                        )
                        """,
                new MapSqlParameterSource()
                        .addValue("hackathonId", hackathonId)
                        .addValue("userId", userId),
                Boolean.class);

        return Boolean.TRUE.equals(result);
    }

    public boolean isOnTeam(UUID hackathonId, UUID userId) {
        Boolean result = jdbc.queryForObject(
                """
                        SELECT EXISTS(
                            SELECT 1 FROM team_memberships
                            WHERE hackathon_id = :hackathonId AND
                            user_id = :userId
                        )
                        """,
                new MapSqlParameterSource()
                        .addValue("hackathonId", hackathonId)
                        .addValue("userId", userId),
                Boolean.class);

        return Boolean.TRUE.equals(result);
    }

    public boolean teamNameExists(UUID hackathonId, String name) {
        Boolean result = jdbc.queryForObject(
                """
                        SELECT EXISTS (
                            SELECT 1 FROM teams
                            WHERE hackathon_id = :hackathonId
                            AND LOWER(TRIM(name)) = LOWER(TRIM(:name))
                            AND status <> 'CANCELLED'
                        )
                        """,
                new MapSqlParameterSource()
                        .addValue("hackathonId", hackathonId)
                        .addValue("name", name),
                Boolean.class);
        return Boolean.TRUE.equals(result);
    }

    private static final RowMapper<Team> TEAM_MAPPER = (rs, rowNum) -> new Team(
            rs.getObject("id", UUID.class),
            rs.getObject("hackathon_id", UUID.class),
            rs.getString("name"),
            rs.getString("description"),
            rs.getObject("leader_user_id", UUID.class),
            TeamStatus.valueOf(rs.getString("status")),
            toInstant(rs.getObject("created_at", OffsetDateTime.class)),
            toInstant(rs.getObject("finalized_at", OffsetDateTime.class)));

    private static Instant toInstant(OffsetDateTime value) {
        return value == null ? null : value.toInstant();
    }

    public Team insertTeam(UUID hackathonId, String name, String description, UUID leaderUserId) {
        return jdbc.queryForObject(
                """
                        INSERT INTO teams (hackathon_id, name, description, leader_user_id)
                        VALUES (:hackathonId, :name, :description, :leaderUserId)
                        RETURNING id, hackathon_id, name, description, leader_user_id,
                                  status, created_at, finalized_at
                        """,
                new MapSqlParameterSource()
                        .addValue("hackathonId", hackathonId)
                        .addValue("name", name)
                        .addValue("description", description)
                        .addValue("leaderUserId", leaderUserId),
                TEAM_MAPPER);
    }

    public void insertTeamMembership(UUID hackathonId, UUID teamId, UUID userId) {
        jdbc.update(
                """
                        INSERT INTO team_memberships (team_id, hackathon_id, user_id)
                        VALUES (:teamId, :hackathonId, :userId)
                        """,
                new MapSqlParameterSource()
                        .addValue("teamId", teamId)
                        .addValue("hackathonId", hackathonId)
                        .addValue("userId", userId));
    }
}
