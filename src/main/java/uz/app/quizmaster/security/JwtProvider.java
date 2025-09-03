package uz.app.quizmaster.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String key;

    @Value("${jwt.expire-timeout}")
    private Long expireTimeout;

    // ✅ Token yaratish (email va rol bilan)
    public String generateToken(String email, String role) {
        Date expiryDate = new Date(System.currentTimeMillis() + expireTimeout);

        return Jwts.builder()
                .setIssuedAt(new Date())
                .setSubject(email)
                .claim("role", role)  // endi role parametrdan keladi
                .setExpiration(expiryDate)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    // ✅ Token ichidan email olish
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // ✅ Token ichidan role olish
    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    // ✅ Claims qaytarish
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ Kalit olish
    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }
}
