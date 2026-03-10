package com.srirathna.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/bookings/booked-dates").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public OncePerRequestFilter jwtAuthFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {

                String path = request.getRequestURI();
                String method = request.getMethod();

                // Skip public paths
                if ("OPTIONS".equals(method) || path.startsWith("/api/auth/")
                        || (path.equals("/api/bookings/booked-dates") && "GET".equals(method))) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String authHeader = request.getHeader("Authorization");

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    sendUnauthorized(response, "Authorization header missing");
                    return;
                }

                String token = authHeader.substring(7).trim();
                try {
                    if (jwtUtil.validateToken(token)) {
                        String mobile = jwtUtil.extractMobile(token);
                        String role = jwtUtil.extractRole(token);
                        var authentication = new org.springframework.security.authentication
                                .UsernamePasswordAuthenticationToken(
                            mobile, null,
                            List.of(new org.springframework.security.core.authority
                                    .SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        );
                        org.springframework.security.core.context.SecurityContextHolder
                                .getContext().setAuthentication(authentication);
                    } else {
                        sendUnauthorized(response, "Invalid or expired token");
                        return;
                    }
                } catch (Exception e) {
                    sendUnauthorized(response, "Token error: " + e.getMessage());
                    return;
                }

                filterChain.doFilter(request, response);
            }

            private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"" + message + "\",\"data\":null}");
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept",
                "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}