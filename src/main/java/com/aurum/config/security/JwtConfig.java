package com.aurum.config.security;


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
	SecretKey jwtSecretKey(@Value("${aurum.security.jwt.secret}") String secret) {
	    // Usamos getBytes() en lugar de Base64.getDecoder()
	    byte[] secretBytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
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
