package com.oauth.googlesociallogin.security.config.oauth2.handlers;

import com.oauth.googlesociallogin.domain.constants.RedirectUris;
import com.oauth.googlesociallogin.security.config.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.oauth.googlesociallogin.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.net.URI;

import static com.oauth.googlesociallogin.security.config.oauth2.repository.HttpCookieOAuth2AuthorizationRequestRepository.OAUTH2_REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@AllArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final HttpCookieOAuth2AuthorizationRequestRepository cookieRepository;
    private final RedirectUris authorizedRedirectUris;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication
    ) throws IOException {
        var targetUrl = determineTargetUrl(request, response, authentication);

        if (!response.isCommitted()) {
            clearAuthenticationAttributes(request, response);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        var redirectUri = CookieUtils.getCookie(request, OAUTH2_REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            return null;
        }

        var targetUri = redirectUri.orElse(getDefaultTargetUrl());
        var token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzIiwiaWF0IjoxNzA2ODc5MzEyLCJleHAiOjE3MDc3NDMzMTJ9.3YY1QvqG-9WCcH_zprn3gQkGzWAva8wr1-MDY94tj1ZEEGsqMmRWnJy7EqqMy5UzT1JkDkVuAoUEcjdi4ByCwQ";

        return UriComponentsBuilder.fromUriString(targetUri)
                .queryParam("token", token)
                .build()
                .toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        cookieRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        var clientRedirectUri = URI.create(uri);

        return authorizedRedirectUris.authorizedRedirectUrls()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    var authorizedURI = URI.create(authorizedRedirectUri);

                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}
