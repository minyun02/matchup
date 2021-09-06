package com.matchup.home.config.auth;

import com.matchup.home.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity //spring security 설정들을 활성화해준다
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .csrf().disable()
                .headers().frameOptions().disable()//h2-console 화면을 사용하기 위해 해당 옵션들을 disable합니다.
                .and()
                    .authorizeRequests()//url별 권한 관리를 설정하는 옵션의 시작점 //authorizeRequests가 선언되어야만 antMatchers 옵션을 사용가능
                    .antMatchers("/", "/css?**", "/images/**", "/js/**", "/h2-console/**").permitAll()//
                    .antMatchers("/api/v1/**").hasRole(Role.USER.name())//권한 관리 대상을 지정하는 옵션 //url, http 메소드별로 관리가 가능
                // "/"등 지정된 url들은 permitAll() 옵션을 통해 전체 열람 권한을 주었다. // "/api/v1/**" 주소를 가진 api는 user 권한을 가진 사람만 가능하도록 했습니다.
                    .anyRequest().authenticated()//설정된 값들 이외 나머지 url들을 나타냅니다. 여기서는 authenticated()를 추가하여 나머지 url들 모두 인증된 사용자들에게만 허용합니다. 인증된 사용자 즉, 로그인한 사용자들을 이야기합니다.
                .and()
                    .logout()
                        .logoutSuccessUrl("/") //로그아울 기능에 대한 여러 설정의 진입점. 로그아웃 성공 시 / 주소로 이동
                .and()
                    .oauth2Login()//oauth2 로그인 성공 기능에 대한 여러 설정의 진입점
                        .userInfoEndpoint()//oauth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정들을 담당
                            .userService(customOAuth2UserService); //소셜 로그인 성공 시 후속 조치를 진행할 userservice 인터페이스의 구현체를 등록합니다.
                                                                    //리소스 서버(즉, 소셜서비스)에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능을 명시할 수 있다.
    }
}
