package org.example.parkease.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("dev")
public class DevSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(devCors()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                HttpMethod.OPTIONS, "/**"
                        ).permitAll()

                        // PUBLIC ENDPOINTS
                        .requestMatchers(
                                "/api/v1/health",
                                "/api/v1/auth/register",
                                "/api/v1/auth/login"
                        ).permitAll()

                        // SWAGGER
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // WEBSOCKET
                        .requestMatchers("/ws/**").permitAll()

                        // ADMIN ONLY
                        .requestMatchers("/api/v1/admin/**")
                        .hasRole("ADMIN")

                        // OWNER ONLY - CREATE PARKING LOT
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/parking-lots"
                        ).hasRole("OWNER")

                        // OWNER ONLY - DELETE PARKING LOT
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/parking-lots/**"
                        ).hasRole("OWNER")

                        // OWNER ONLY - CREATE SLOT
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/parking-slots"
                        ).hasRole("OWNER")

                        // OWNER ONLY - DELETE SLOT
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/parking-slots/**"
                        ).hasRole("OWNER")

                        // BOOKING ENDPOINTS
                        // Create a booking -> only regular users should create bookings
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/bookings"
                        ).hasRole("USER")

                        // Complete a booking -> only the booking owner (role USER) should complete their booking
                        // pattern uses /** to match the {bookingId}/complete segment
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/bookings/**"
                        ).hasAnyRole( "ADMIN", "USER")

                        // Get my bookings -> user can list their own bookings
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/bookings/my"
                        ).hasRole("USER")

                        // Get all bookings -> only ADMIN can list all bookings
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/bookings",
                                "/api/v1/bookings/**"
                        ).hasRole("ADMIN")

                        // ALL LOGGED-IN USERS
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/parking-lots/**"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/parking-slots/**"
                        ).authenticated()

                        // EVERYTHING ELSE
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/api/v1/auth/login")
                        // No loginPage() — frontend owns the login UI
                        .successHandler((req, res, auth) ->
                                res.setStatus(200))                 // don't redirect, return 200
                        .failureHandler((req, res, ex) ->
                                res.sendError(401, "Bad credentials"))
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .logoutSuccessHandler((req, res, auth) ->
                                res.setStatus(200))                 // don't redirect on logout
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) ->
                                res.sendError(401, "Unauthorized")) // no redirect to /login
                );
        return http.build();

    }

    @Bean
    public CorsConfigurationSource devCors() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173",
                        "http://127.0.0.1:5173"
                )
        );
        configuration.setAllowedMethods(List.of("GET", "PUT", "POST", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // needed for session cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}