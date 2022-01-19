package Utilities;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtHelper {
    // static Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode("vietnamvodichvjppro1234"));
    static Key key;
    static{
        try {
            InitialContext initialContext = new InitialContext();
            Context envContext = initialContext.lookup("java:comp/env");
        } catch (NamingException e) {
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
