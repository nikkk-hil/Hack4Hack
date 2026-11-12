package hack4hack_api.team;

import org.springframework.http.HttpStatus;

import hack4hack_api.common.exception.ApiException;

public class NotAParticipantException extends ApiException {
    
    public NotAParticipantException() {
        super(HttpStatus.FORBIDDEN, "NOT_A_PARTICIPANT",
        "You are not registered for this hackathon.");
    }
}
