package com.oauth.googlesociallogin.domain.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

@ConfigurationProperties(prefix = "app")
public record RedirectUris(List<String> authorizedRedirectUrls) {
}
