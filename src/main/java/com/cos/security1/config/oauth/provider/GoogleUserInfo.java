package com.cos.security1.config.oauth.provider;

import lombok.NoArgsConstructor;

import java.util.Map;

public class GoogleUserInfo implements OAuth2UserInfo{

    //oauth2User.getAttributes() 받기 위해서
    private Map<String, Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
