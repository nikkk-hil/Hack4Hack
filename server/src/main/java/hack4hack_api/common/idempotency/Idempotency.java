package hack4hack_api.common.idempotency;

public record Idempotency(
    String requestHash,
    Integer responseStatus,
    String responseBody
) {}
