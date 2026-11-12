package hack4hack_api.hackathon;

import org.springframework.http.HttpStatus;

import hack4hack_api.common.exception.ApiException;

public class RegistrationNotOpenException extends ApiException {

    public RegistrationNotOpenException() {
        super(HttpStatus.CONFLICT, "REGISTRATION_NOT_OPEN",
                "Registration is not open for this hackathon.");
    }
}