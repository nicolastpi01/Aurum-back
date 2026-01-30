package com.aurum.infrastructure.security;


import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.*;

import com.nimbusds.jose.jwk.source.ImmutableSecret;


@Configuration
public class JwtConfig {
	
	 @Bean
	  SecretKey jwtSecretKey(@Value("${aurum.security.jwt.secret}") String base64Secret) {
	    byte[] secretBytes = Base64.getDecoder().decode(base64Secret);
	    return new SecretKeySpec(secretBytes, "HmacSHA256");
	  }

	  @Bean
	  JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
	    return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
	  }

	  @Bean
	  JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
	    return NimbusJwtDecoder.withSecretKey(jwtSecretKey).build();
	  }

}
