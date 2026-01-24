package com.tcnw.ELH_api_gateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // 1. Cho phép API Đăng ký user không cần login
                        .pathMatchers("/api/v1/users/register").permitAll()

                        // 2. Cho phép Swagger hoặc Actuator (nếu cần debug)
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // 3. Tất cả request còn lại bắt buộc phải có Token
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {})); // Xác thực JWT

        return http.build();
    }
}