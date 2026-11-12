package hack4hack_api.team.dto;

import java.time.Instant;
import java.util.UUID;

import hack4hack_api.team.Team;
import hack4hack_api.team.TeamStatus;

public record TeamResponse(
        UUID id,
        UUID hackathonId,
        String name,
        String description,
        UUID leaderId,
        TeamStatus status,
        Instant createdAt,
        Instant finalizedAt
) {

    public static TeamResponse from(Team team) {
        return new TeamResponse(
                team.id(),
                team.hackathonId(),
                team.name(),
                team.description(),
                team.leaderUserId(),
                team.status(),
                team.createdAt(),
                team.finalizedAt());
    }
}