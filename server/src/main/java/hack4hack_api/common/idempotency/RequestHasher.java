package hack4hack_api.common.idempotency;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;

public final class RequestHasher {

    public static final String CREATE_TEAM = "CREATE_TEAM";

    private RequestHasher() {}

    public static String sha256Hex(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    public static String forCreateTeam(UUID hackathonId, String name, String description) {
        Objects.requireNonNull(hackathonId, "hackathonId");
        Objects.requireNonNull(name, "name");

        String canonical = String.join("|",
                CREATE_TEAM,
                hackathonId.toString(),
                field(name),
                field(description)
        );

        return sha256Hex(canonical);
    }

    private static String field(String value) {
        return value == null ? "-" : value.length() + ":" + value;
    }

}