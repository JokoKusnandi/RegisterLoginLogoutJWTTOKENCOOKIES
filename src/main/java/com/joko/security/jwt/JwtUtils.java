package com.joko.security.jwt;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.joko.security.services.UserDetailsImpl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	  @Value("${joko.app.jwtSecret}")
	  private String jwtSecret;

	  @Value("${joko.app.jwtExpirationMs}")
	  private int jwtExpirationMs;

	  @Value("${joko.app.jwtCookie}")
	  private String jwtCookie;

	  public String getJwtFromCookies(HttpServletRequest request) {
	    Cookie cookie = WebUtils.getCookie(request, jwtCookie);
	    if (cookie != null) {
	      return cookie.getValue();
	    } else {
	      return null;
	    }
	  }

	  public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
	    String jwt = generateTokenFromUsername(userPrincipal.getUsername());
	    ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(24 * 60 * 60).httpOnly(true).build();
	    return cookie;
	  }

	  public ResponseCookie getCleanJwtCookie() {
	    ResponseCookie cookie = ResponseCookie.from(jwtCookie, null).path("/api").build();
	    return cookie;
	  }

	  public String getUserNameFromJwtToken(String token) {
	    return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody().getSubject();
	  }
	  
	  private Key getSignInKey() {
		    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
		    return Keys.hmacShaKeyFor(keyBytes);
	  }

	  public boolean validateJwtToken(String authToken) {
	    try {
	      Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parse(authToken);
	      return true;
		} /*
			 * catch (SignatureException e) { logger.error("Invalid JWT signature: {}",
			 * e.getMessage()); }
			 */ 
	    catch (MalformedJwtException e) {
	      logger.error("Invalid JWT token: {}", e.getMessage());
	    } catch (ExpiredJwtException e) {
	      logger.error("JWT token is expired: {}", e.getMessage());
	    } catch (UnsupportedJwtException e) {
	      logger.error("JWT token is unsupported: {}", e.getMessage());
	    } catch (IllegalArgumentException e) {
	      logger.error("JWT claims string is empty: {}", e.getMessage());
	    }

	    return false;
	  }
	  
	  public String generateTokenFromUsername(String username) {
	    return Jwts.builder()
	        .setSubject(username)
	        .setIssuedAt(new Date())
	        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
	        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
	        .compact();
	  }
	
}
