package ecommerce_escribo.demo.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

public class SupabaseAuthUtil {
    public static String extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        String token = authHeader.substring(7);
        DecodedJWT decoded = JWT.decode(token);
        return decoded.getSubject();
    }
}
