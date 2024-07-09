package com.cos.security1.auth;

import com.cos.security1.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;  // Composition

    /**
     * 세션정보를 한번 찍어보면
     * User오브젝트가 Map<String, Object> 로구성되어 있음.
     * 이걸 통쨰로 넣어버리기 위해서 아래의 attributes를 추가함.
     */
    private Map<String, Object> attributes;

    /**
     * 일반 로그인용 생성자
     */
    public PrincipalDetails(User user) {
        this.user = user;
    }

    /**
     * OAuth 로그인용 생성자
     */
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public String getName() {
        return null;
    }

    /**
     * 해당 User의 권한을 리턴하는 곳임.
     * 근데 우리가 만든 User의 role은 String이라 이걸 Collection<GrantedAut...>에 넣어주는거임.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 이런식으로 user.getRole을 넣어주면 됨.
        ArrayList<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });

        return collect;
    }

    /**
     * 여기는 Password를 return하는 자리임.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 아이디 리턴
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 니 계정이 만료안됐니?
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 니 계정이 안잠겼니?
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 니 계정의 비밀번호가 1년이 안지났니?
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 니 계정이 활성화 되어있니?
     * 휴먼 계정같은거 설정할 때
     */
    @Override
    public boolean isEnabled() {
        /*
        1년 동안 회원이 로그인을 안하면 휴먼 계정을 설정할 때,
        user에 loginDate같은거 만들어놓고 현재시간-loginDate해서 return false하면 됨.
         */
        return true;
    }


}
