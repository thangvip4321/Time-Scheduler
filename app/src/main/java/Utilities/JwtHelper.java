package Utilities;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import io.jsonwebtoken.Jwts;


public class JwtHelper {
    // static Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode("vietnamvodichvjppro1234"));
    static Key key;
    static{
        try {
            InitialContext initialContext = new InitialContext();
            Context envContext = (Context) initialContext.lookup("java:comp/env");
            String password = (String) envContext.lookup("pwKey");
            KeyStore ks = KeyStore.getInstance(new File("src/main/resources/test.jks"), password.toCharArray());
            key = ks.getKey("mykey", password.toCharArray());
        } catch (NamingException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    Map<String,Object> claim;
    public JwtHelper(){
        claim = new HashMap<String,Object>();
    }
    public JwtHelper put(String key,Object value) {
        claim.put(key, value);
        return this;
    }
    public static String createTokenWithJsonClaim(HashMap<String,?> claim) {
        String token = Jwts.builder().setClaims(claim).signWith(key).compact();
        return token;
    }
    public String createToken() {
        return Jwts.builder().setClaims(claim).signWith(key).compact();
    }
    public Map<String,Object> parseToken(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
