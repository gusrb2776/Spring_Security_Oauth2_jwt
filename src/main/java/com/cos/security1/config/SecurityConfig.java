package com.cos.security1.config;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
// 시큐리티 활성화 : 정확히는 우리가 지금 만드는 스프링 시큐리티 필터가 스프링 필터체인에 등록됨
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                // csrf 비활성화
                .csrf(csrf->csrf.disable())
                // `/user`로 들어오면 권한 확인필요
                // `/manager`로 들어오면 권한이 필요 (ADMIN이나 MANAGER)
                // `/admin`로 들어오면 권한이 필요 (ADMIN)
                // 그외에는 모두 허용
                .authorizeHttpRequests( authz -> authz
                        .requestMatchers("/user/**").authenticated()
                        .requestMatchers("/manager/**").hasAnyRole("ADMIN","MANAGER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                // config가 없을땐 알아서 스프링 시큐리티가 낚아채서 로그인페이지로 갔는데 config가 있으니 그걸 안해줘.
                // 이렇게 하면 권한이 없는녀석을 다시 스프링 시큐리티가 낚아채서 login으로 가게 만들어버림.
                .formLogin( form -> form
                                .loginPage("/loginForm")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/")
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/loginForm")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(principalOauth2UserService)
                        )
                );

        return http.build();
    }
}
