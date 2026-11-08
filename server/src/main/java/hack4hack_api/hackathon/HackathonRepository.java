package hack4hack_api.hackathon;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository 
public class HackathonRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public HackathonRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Hackathon> findById(UUID id) {
        List<Hackathon> rows = jdbc.query(
            """
            SELECT id, name,description, min_team_size, max_team_size, registration_opens_at, registration_closes_at, status, created_at
            FROM hackathons
            WHERE id = :id
            """,
            new MapSqlParameterSource("id", id),
            (rs, rowNum) -> new Hackathon(
                rs.getObject("id", UUID.class),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("min_team_size"),
                rs.getInt("max_team_size"),
                rs.getObject("registration_opens_at", OffsetDateTime.class).toInstant(),
                rs.getObject("registration_closes_at", OffsetDateTime.class).toInstant(),
                HackathonStatus.valueOf(rs.getString("status")),
                rs.getObject("created_at", OffsetDateTime.class).toInstant()
            )
        );  
        
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }
}

