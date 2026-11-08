package hack4hack_api.common.exception;

public record ErrorResponse(int status, String code, String message, String traceId) {
    
}
