package hack4hack_api.common.idempotency;

import org.springframework.http.HttpStatus;

import hack4hack_api.common.exception.ApiException;

public class IdempotencyKeyReusedException extends ApiException{
    
    public IdempotencyKeyReusedException() {
        super(HttpStatus.CONFLICT, "IDEMPOTENCY_KEY_REUSED",
        "This Idempotency-Key was already used for a different request.");
    }
}
