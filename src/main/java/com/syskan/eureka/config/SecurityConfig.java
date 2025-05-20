package com.syskan.eureka.config;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:}")
	private String jwkSetUri;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// Configure security for the Eureka server
		http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						// Public endpoints
						.requestMatchers("/", "/eureka/**", "/actuator/health/**", "/actuator/info").permitAll()
						// Swagger UI and API docs
						.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**").permitAll()
						// H2 console (for development)
						.requestMatchers("/h2-console/**").permitAll()
						// Secure all other endpoints
						.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.oauth2ResourceServer(
						oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
				.oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/", true))
				// Support for H2 console frame options
				.headers(headers -> headers.frameOptions().sameOrigin());

		return http.build();
	}

	@Bean
	public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
		JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
		return jwtConverter;
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		// Use JWK Set URI if available, otherwise use the secret
		if (jwkSetUri != null && !jwkSetUri.isEmpty()) {
			return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
		} else {
			byte[] secretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
			SecretKey secretKey = new SecretKeySpec(secretBytes, 0, secretBytes.length, "HmacSHA256");

			return NimbusJwtDecoder.withSecretKey(secretKey).build();
//            return NimbusJwtDecoder.withSecretKey(javax.crypto.spec.Keys.hmacShaKeyFor(jwtSecret.getBytes())).build();
		}
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Collections.singletonList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
		configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
