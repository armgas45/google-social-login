package com.oauth.googlesociallogin.security.config;

import com.oauth.googlesociallogin.security.config.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.oauth.googlesociallogin.security.config.oauth2.handlers.OAuth2AuthenticationFailureHandler;
import com.oauth.googlesociallogin.security.config.oauth2.handlers.OAuth2AuthenticationSuccessHandler;
import com.oauth.googlesociallogin.service.oauth.OAuth2UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@AllArgsConstructor
public class GlobalSecurityConfig {
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler successHandler;
    private final OAuth2AuthenticationFailureHandler failureHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(configurer -> configurer.anyRequest().authenticated());

        http.oauth2Login(customizer -> customizer
                .authorizationEndpoint(config ->
                        config.baseUri("/oauth2/authorize")
                                .authorizationRequestRepository(authorizationRequestRepository)
                )
                .redirectionEndpoint(config -> config.baseUri("/oauth2/callback/*"))
                .userInfoEndpoint(config -> config.userService(oAuth2UserService))
                .successHandler(successHandler)
                .failureHandler(failureHandler)
        );

        return http.build();
    }
}
