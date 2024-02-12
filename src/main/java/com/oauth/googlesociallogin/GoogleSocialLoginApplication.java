package com.oauth.googlesociallogin;

import com.oauth.googlesociallogin.domain.constants.RedirectUris;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RedirectUris.class)
public class GoogleSocialLoginApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoogleSocialLoginApplication.class, args);
    }
}
