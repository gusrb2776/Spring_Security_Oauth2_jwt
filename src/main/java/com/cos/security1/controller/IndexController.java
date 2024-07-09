package com.cos.security1.controller;

import com.cos.security1.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    public @ResponseBody String testLogin(
            Authentication authentication,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        System.out.println("/test/login====================");
        System.out.println("authentication.getPrincipal() = " + authentication.getPrincipal());
        // 위에 친구 리턴타입이 Object임. 그러니 한번 우리가만든 PrincipalDetails로 바꿔보자

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("principalDetails.getUser() = " + principalDetails.getUser());

        System.out.println("userDetails = " + userDetails.getUsername());
        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(
            Authentication authentication,
            @AuthenticationPrincipal OAuth2User oauth
    ) {
        System.out.println("/test/oauth/login====================");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());

        System.out.println("oauth = " + oauth.getAttributes());

        return "OAuth 세션 정보 저장";
    }

    @GetMapping({"", "/"})
    public String index() {
        return "index";
    }

    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails = " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

//    @GetMapping("/joinProc")
//    public @ResponseBody String joinProc(){
//        return "회원가입 완료";
//    }


    @PostMapping("/join")
    public @ResponseBody String join(User user) {
        System.out.println("user = " + user);
        user.setRole("USER");
//        userRepository.save(user);      // 회원가입이 잘됨. 근데, 비밀번호가 1234 이렇게 등록이 됨...
        // 이런식으로 비밀번호가 설정되면 패스워드가 암호화가 안되어서 시큐리티로 로그인이 안됨.
        // 이를 위해 SecurityConfig에 비밀번호 암호화하는 코드를 추가하자.

        String rawPassword = user.getPassword();
        String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);

        userRepository.save(user);

        return "redirect:/loginForm";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }
}
