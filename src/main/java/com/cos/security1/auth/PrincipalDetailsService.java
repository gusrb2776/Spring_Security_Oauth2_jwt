package com.cos.security1.auth;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 앞의 PrincipleDetails는 우리가 나중에 강제로 메모리에 띄울꺼고 얘만 미리 Service로 뛰어놓자.
 *
 * 시큐리티 설정에서 loginProessingUrl("/login");을 해놓음
 * 그래서 login요청이 오면 자동으로 UserDetailsService타입으로 IoC되어있는 loadUserByUsername()함수가 실행됨.
 */
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // 임마가 자동으로 호출 되는거임.
    // 여기 parameter가 실제 프론트에서 input으로 넘어오는 값이 있는 태그의 name과 같아야함.

    // 찾아서 UserDetails가 어디로 리턴될까?
    // 시큐리티 session이 있고 여기 들어갈수있는게 Authentication타입이고 이 안에 UserDetails타입이 들어와야 해.
    // 우리가 만든 PrincipalDetails는 UserDetails타입이고. 그래서 이게 리턴되면서 Authentication안에 들어감.
    // 그리고 이 Authentication객체가 또 시큐리티 Session안에 들어감.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByUsername(username);

        if(userEntity != null) {
            // 여기서 직접 new로 PrincipalDetails를 해줌으로 굳이 IoC안해도 필요시 메모리에 있음.
            return new PrincipalDetails(userEntity);
        }
        return null;
    }

}
