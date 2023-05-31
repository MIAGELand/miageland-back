package fr.miage.MIAGELand.security;

/**
 * Utility class for extracting data from HTTP headers.
 */
public class Headers {

    /**
     * Extracts the email from the Authorization header.
     * @param authorizationHeader The Authorization header value
     * @return The email
     */
    public static String extractEmailFromAuthorizationHeader(String authorizationHeader) {
        // Assuming the header value is in the format "email=admin"
        String[] headerParts = authorizationHeader.split("=");

        if (headerParts.length == 2 && "email".equals(headerParts[0])) {
            return headerParts[1];
        }

        // Handle invalid or missing header value
        throw new IllegalArgumentException("Invalid Authorization header");
    }
}
