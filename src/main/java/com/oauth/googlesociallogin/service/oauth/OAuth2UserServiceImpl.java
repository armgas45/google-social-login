package com.oauth.googlesociallogin.service.oauth;

import com.oauth.googlesociallogin.domain.constants.UserEntity;
import com.oauth.googlesociallogin.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import java.util.Collections;

@Component
@AllArgsConstructor
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        return processOAuth2User(userRequest, super.loadUser(userRequest));
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        if (userRequest == null || oAuth2User == null) return null;

        var email = (String) oAuth2User.getAttributes().get("email");
        var image = (String) oAuth2User.getAttributes().get("picture");
        var username = (String) oAuth2User.getAttributes().get("name");

        var user = userService.findByEmail(email);
        UserEntity foundUser;

        if (user.isPresent()) {
            foundUser = userService.updateUser(user.get(), username, image);
        } else {
            foundUser = userService.registerUser(username, "PASSWORD", image, email);
        }

        return new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                "name"
        );
    }
}
