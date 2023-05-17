package fr.miage.MIAGELand.security;

public class Headers {

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
