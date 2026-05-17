package vn.khanguyen.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationEntryPoint caep)
                        throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .cors(Customizer.withDefaults())
                                .authorizeHttpRequests(
                                                authz -> authz
                                                                .requestMatchers("/", "/auth/login", "/v3/api-docs/**",
                                                                                "/storage/**",
                                                                                "/auth/register",
                                                                                "/auth/register/hr",
                                                                                "/auth/refresh",
                                                                                "/swagger-ui/**",
                                                                                "/swagger-ui.html")
                                                                .permitAll()
                                                                .anyRequest().authenticated()

                                )
                                // khai bao them filter de xac thuc token
                                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
                                                .authenticationEntryPoint(caep))

                                .formLogin(f -> f.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();

        }
}
