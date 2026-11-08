package hack4hack_api.hackathon;

import java.util.UUID;

import org.springframework.http.HttpStatus;

import hack4hack_api.common.exception.ApiException;

public class HackathonNotFoundException extends ApiException {
    public HackathonNotFoundException(UUID id) {
        super(HttpStatus.NOT_FOUND, "HACKATHON_NOT_FOUND", "Hackathon not found");
    }
}
