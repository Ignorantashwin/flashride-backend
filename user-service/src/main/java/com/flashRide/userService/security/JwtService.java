package com.flashRide.userService.security;

import com.flashRide.userService.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Struct;
import java.util.Base64;
import java.util.Date;


@Service
public class JwtService {

private PrivateKey privateKey;
private PublicKey publicKey;

public JwtService() throws Exception{
     this.privateKey= getPrivateKey();
     this.publicKey= getPublicKey();
}

private PrivateKey getPrivateKey() throws Exception {
    String key = Files.readString(Paths.get("user-service/keys/private.pem"));
    key = key.replace("-----BEGIN PRIVATE KEY-----","")
            .replace("-----END PRIVATE KEY-----","")
            .replaceAll("\\s","");
    byte[] keyBytes = Base64.getDecoder().decode(key);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePrivate(spec);

}
private PublicKey getPublicKey() throws Exception{
    String key = Files.readString(Paths.get("user-service/keys/public.pem"));
    key = key.replace("-----BEGIN PUBLIC KEY-----","")
            .replace("-----END PUBLIC KEY-----","")
            .replaceAll("\\s","");
    byte[] getBytes = Base64.getDecoder().decode(key);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(getBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePublic(spec);
}
public String generateAccessToken(User user){
    return Jwts.builder()
            .subject(user.getUserName())
            .claim("userId", user.getUserId())
            .claim("role", user.getRole().name())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 1000*60*15))
            .signWith(privateKey, Jwts.SIG.RS256)
            .compact();
}

public String getUsernameFromToken(String token){
   Claims claims = Jwts.parser()
           .verifyWith(publicKey)
           .build()
           .parseSignedClaims(token)
           .getPayload();
   return claims.getSubject();
}
}
