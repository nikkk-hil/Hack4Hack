package hack4hack_api.hackathon.dto;

import hack4hack_api.hackathon.Hackathon;
import hack4hack_api.hackathon.HackathonStatus;
import java.time.Instant;
import java.util.UUID;


public record HackathonResponse(
    UUID id,
    String name,
    String description,
    int minTeamSize,
    int maxTeamSize,
    Instant registrationOpensAt,
    Instant registrationClosesAt,
    HackathonStatus status,
    Instant createdAt
) {
    public static HackathonResponse from(Hackathon h){
        return new HackathonResponse(h.id(), h.name(), h.description(), h.minTeamSize(), h.maxTeamSize(), h.registrationOpensAt(), h.registrationClosesAt(), h.status(), h.createdAt());
    }
}
