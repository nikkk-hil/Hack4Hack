package hack4hack_api.team;

import org.springframework.http.HttpStatus;

import hack4hack_api.common.exception.ApiException;

public class TeamNameExistsException extends ApiException {
    
    public TeamNameExistsException() {
        super(HttpStatus.CONFLICT, "TEAM_NAME_EXISTS",
        "A team with this name already exists in this hackathon.");

    }
}
