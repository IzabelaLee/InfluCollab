package com.influcollab.config;

import com.influcollab.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - documentation
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Public endpoints - authentication
                        .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()

                        // Public endpoints - user signup
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()

                        // Public endpoints - browse all opportunities
                        .requestMatchers(HttpMethod.GET, "/opportunities").permitAll()
                        .requestMatchers(HttpMethod.GET, "/opportunities/**").permitAll()

                        // Public endpoints - browse user's opportunities (read-only)
                        .requestMatchers(HttpMethod.GET, "/users/*/opportunities").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/*/opportunities/**").permitAll()

                        // Protected endpoints - user operations (write)
                        .requestMatchers(HttpMethod.PUT, "/users/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/users/**").authenticated()

                        // Protected endpoints - opportunity operations (write)
                        .requestMatchers(HttpMethod.POST, "/users/*/opportunities").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/*/opportunities/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/users/*/opportunities/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/users/*/opportunities/**").authenticated()

                        // Default: require authentication for anything else
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
