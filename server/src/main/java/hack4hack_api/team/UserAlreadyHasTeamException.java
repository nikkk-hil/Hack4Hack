package hack4hack_api.team;

import org.springframework.http.HttpStatus;

import hack4hack_api.common.exception.ApiException;

public class UserAlreadyHasTeamException extends ApiException{
    
    public UserAlreadyHasTeamException() {
        super(HttpStatus.CONFLICT, "USER_ALREADY_HAS_TEAM",
        "You already belong to a team in this hackathon.");
    }
}
