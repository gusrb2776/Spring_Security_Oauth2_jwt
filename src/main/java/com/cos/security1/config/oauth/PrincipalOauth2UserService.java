package com.cos.security1.config.oauth;

import com.cos.security1.auth.PrincipalDetails;
import com.cos.security1.config.oauth.provider.GoogleUserInfo;
import com.cos.security1.config.oauth.provider.NaverUserInfo;
import com.cos.security1.config.oauth.provider.OAuth2UserInfo;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

//    @Autowired
//    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;


    /**
     * 구글로 부터 받은 userRequest 데이터에 대한 후처리 함수
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest = " + userRequest.getClientRegistration());
        System.out.println("userRequest = " + userRequest.getAccessToken());
        System.out.println("userRequest = " + super.loadUser(userRequest).getAttributes());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());

        // 회원가입을 진행시켜보자.
        String provider = userRequest.getClientRegistration().getRegistrationId();        // 구글

        OAuth2UserInfo oAuth2UserInfo = null;
        if(provider.equals("google")){
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if(provider.equals("naver")){
            System.out.println("네이버 로그인 요청");
            // response : {
            //  resultcode=00, ....
            //  response={ id=asdfasdfas , email=sdfasdfasf.. }
            // 이런식의 구조라 두번째 response에서 빼줄려고 이렇게 함.
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        }else{
            System.out.println("우리는 구글/ 네이버만 지원해요");
        }

        String providerId = oAuth2UserInfo.getProviderId();                 // 구글 아이디 (이거 user찍어보면 sub란걸 알 수 있음)
        String username = provider + "_" + providerId;                      // 구글_구글아이디 이런식으로 가입시킬꺼야.
//        String password = bCryptPasswordEncoder.encode("겟인데어");        // 사실 비밀번호 만들기는 크게 의미없음.
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);
        if(userEntity == null) {
            userEntity = User.builder()
                    .username(username)
//                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            userRepository.save(userEntity);
        }

        // PrincipalDetails가 OAuth2User와 UserDetails를 모두 구현해서 이렇게 리턴이 가능.
        // 그럼 이제 OAuth로 가입을 하면 attibutes로 들고 나가는거지!
        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
