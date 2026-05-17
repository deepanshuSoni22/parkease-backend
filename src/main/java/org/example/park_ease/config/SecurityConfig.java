package org.example.park_ease.config;

import org.example.park_ease.entity.User;
import org.example.park_ease.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {

        return username -> {

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

            return org.springframework.security.core.userdetails.User
                    .builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole().name())
                    .build();
        };

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5500",
                        "http://127.0.0.1:5500",
                        "https://deepanshusoni22.github.io/parkease-frontend/"
                )
        );
        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                HttpMethod.OPTIONS, "/**"
                        ).permitAll()

                        // PUBLIC ENDPOINTS
                        .requestMatchers(
                                "/api/v1/health",
                                "/api/v1/auth/register"
                        ).permitAll()

                        // SWAGGER
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

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
                        ).hasRole("USER")

                        // Get my bookings -> user can list their own bookings
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/bookings/my"
                        ).hasRole("USER")

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
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
