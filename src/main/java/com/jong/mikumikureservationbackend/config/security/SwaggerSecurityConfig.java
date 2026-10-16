package com.jong.mikumikureservationbackend.config.security;

import com.jong.mikumikureservationbackend.config.properties.SwaggerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SwaggerSecurityConfig {

    private final SwaggerProperties swaggerProperties;

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http, PasswordEncoder passwordEncoder)
            throws Exception {
        UserDetails docsUser = User.withUsername(swaggerProperties.getUsername())
                .password(passwordEncoder.encode(swaggerProperties.getPassword()))
                .roles("DOCS")
                .build();
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(new InMemoryUserDetailsManager(docsUser));
        provider.setPasswordEncoder(passwordEncoder);

        http
                .securityMatcher("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                .authenticationManager(new ProviderManager(provider))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
