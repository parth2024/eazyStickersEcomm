package com.eazybytes.eazystore.security;

import com.eazybytes.eazystore.filter.JWTTokenValidatorFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class EazyStoreSecurityConfig {

    private final List<String> publicPaths;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        return http.csrf(csrfConfig -> csrfConfig.
                        csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource())) // Apply global CORS configuration
                .authorizeHttpRequests((requests) -> {
                            // Open public paths without authentication
                            publicPaths.forEach(path ->
                                    requests.requestMatchers(path).permitAll());
                            // Restrict specific paths based on roles
                            requests.requestMatchers("/api/v1/admin/**").hasRole("ADMIN");
                            requests.requestMatchers("/eazystore/actuator/**").hasRole("OPS_ENG");
                            requests.requestMatchers("/swagger-ui.html", "/swagger-ui/**",
                            "/v3/api-docs/**").hasAnyRole("DEV_ENG","QA_ENG");
                            requests.anyRequest().hasAnyRole("USER", "ADMIN"); // Default access for other requests
                        }
                )
                .addFilterBefore(new JWTTokenValidatorFilter(publicPaths), BasicAuthenticationFilter.class)
                .formLogin(withDefaults())
                .httpBasic(withDefaults()).build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
             AuthenticationProvider authenticationProvider) {
        var providerManager = new ProviderManager(authenticationProvider);
        return providerManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Allow requests from any origin
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        config.setAllowCredentials(true); // Allow credentials (cookies, authorization tokens)
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept")); // Allow necessary headers
        config.setExposedHeaders(Arrays.asList("Authorization")); // Expose Authorization header for client access
        config.setMaxAge(3600L); // Caching pre-flight requests for 1 hour

        // Apply the CORS configuration to all URLs
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
