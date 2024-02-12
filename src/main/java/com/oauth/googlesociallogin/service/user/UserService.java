package com.oauth.googlesociallogin.service.user;

import com.oauth.googlesociallogin.domain.constants.UserEntity;
import com.oauth.googlesociallogin.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserEntity registerUser(String username, String password, String imgUrl, String email) {
        var user = UserEntity.builder()
                .email(email)
                .name(username)
                .imageUrl(imgUrl)
                .password(password)
                .build();

        return userRepository.save(user);
    }

    public UserEntity updateUser(UserEntity user, String username, String imgUrl) {
        user.setName(username);
        user.setImageUrl(imgUrl);

        return userRepository.save(user);
    }
}