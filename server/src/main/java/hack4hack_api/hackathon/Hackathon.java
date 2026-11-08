package hack4hack_api.hackathon;

import java.time.Instant;
import java.util.UUID;

public record Hackathon(
    UUID id,
    String name,
    String description,
    int minTeamSize,
    int maxTeamSize,
    Instant registrationOpensAt,
    Instant registrationClosesAt,
    HackathonStatus status,
    Instant createdAt
) {}
