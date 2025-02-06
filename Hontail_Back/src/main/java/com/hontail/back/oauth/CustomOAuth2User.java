package com.hontail.back.oauth;

import com.hontail.back.db.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {
    private final User user;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return user.getUserNickname();
    }

    public String getUserEmail() {
        return user.getUserEmail();
    }

    public String getUserImageUrl() {
        return user.getUserImageUrl();
    }

    public Integer getUserId() {
        return user.getId();
    }

    public Map<String, Object> getUserInfo() {
        return Map.of(
                "id", getUserId(),
                "email", getUserEmail(),
                "nickname", getName(),
                "profileImage", getUserImageUrl()
        );
    }
}