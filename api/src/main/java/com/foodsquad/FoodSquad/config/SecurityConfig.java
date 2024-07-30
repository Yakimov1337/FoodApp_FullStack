package com.foodsquad.FoodSquad.config;

import com.foodsquad.FoodSquad.filter.JwtRequestFilter;
import com.foodsquad.FoodSquad.exception.CustomAccessDeniedHandler;
import com.foodsquad.FoodSquad.exception.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                                // User controller endpoints
                                .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("ADMIN", "MODERATOR")
                                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("ADMIN", "MODERATOR", "NORMAL")
                                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                                // Order controller endpoints
                                .requestMatchers(HttpMethod.GET, "/api/orders/**").hasAnyRole("ADMIN", "MODERATOR", "NORMAL")
                                .requestMatchers(HttpMethod.POST, "/api/orders/**").hasAnyRole("ADMIN", "MODERATOR", "NORMAL")
                                .requestMatchers(HttpMethod.PUT, "/api/orders/**").hasAnyRole("ADMIN", "MODERATOR", "NORMAL")
                                .requestMatchers(HttpMethod.DELETE, "/api/orders/**").hasAnyRole("ADMIN", "MODERATOR", "NORMAL")
                                // MenuItem controller endpoints
                                .requestMatchers(HttpMethod.GET, "/api/menu-items/**").hasAnyRole("ADMIN", "MODERATOR", "NORMAL")
                                .requestMatchers(HttpMethod.POST, "/api/menu-items/**").hasAnyRole("ADMIN", "MODERATOR", "NORMAL")
                                .requestMatchers(HttpMethod.PUT, "/api/menu-items/**").hasAnyRole("ADMIN", "MODERATOR", "NORMAL")
                                .requestMatchers(HttpMethod.DELETE, "/api/menu-items/**").hasAnyRole("ADMIN", "MODERATOR", "NORMAL")
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .accessDeniedHandler(customAccessDeniedHandler)
                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
