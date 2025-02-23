package com.good.physicalexercisesystem.config;

import com.good.physicalexercisesystem.security.CustomAuthenticationFilter;
import com.good.physicalexercisesystem.security.CustomUserDetailsService;
import com.good.physicalexercisesystem.security.JwtAuthenticationFilter;
import com.good.physicalexercisesystem.service.UserService;
import com.good.physicalexercisesystem.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(UserService userService,
                        JwtUtils jwtUtils,
                        CustomUserDetailsService userDetailsService,
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        ObjectMapper objectMapper) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:5173"); // 明确指定前端域名
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = 
            new CustomAuthenticationFilter(authenticationManager, userService, jwtUtils, objectMapper);
        customAuthenticationFilter.setFilterProcessesUrl("/auth/login");

        http
            .csrf().disable()
            .cors().configurationSource(corsConfigurationSource())
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .antMatchers("/auth/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(customAuthenticationFilter)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .userDetailsService(userDetailsService);

        return http.build();
    }
}
