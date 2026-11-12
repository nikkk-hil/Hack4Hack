package hack4hack_api.team;

import java.time.Instant;
import java.util.UUID;

public record Team(
        UUID id,
        UUID hackathonId,
        String name,
        String description,
        UUID leaderUserId,
        TeamStatus status,
        Instant createdAt,
        Instant finalizedAt
) {}