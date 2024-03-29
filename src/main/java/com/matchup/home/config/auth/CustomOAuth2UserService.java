package com.matchup.home.config.auth;

import com.matchup.home.domain.user.User;
import com.matchup.home.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        OAuth2UserService<OAuth2UserRequest, OAuth2User>
                delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate
                                    .loadUser(userRequest);

        String registrationId = userRequest.
                getClientRegistration().getRegistrationId(); //현재 로그인 진행 중인 서비스를 구분하는 코드. 지금은 구글만 사용하는 불필요한 값이지만, 이후 네이버 로그인 시 구분하기 위해 사용
        String userNameAttributeName = userRequest.
                getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();//OAuth2 로그인 진행시 키가 되는 필드값. primary key와 같은 의미

        //oauth2userservice를 통해 가져온 oauth2user의 attribute를 담을 클래스입니다. 이후 네이버 등 다른 소셜 로그인도 이 클래스를 사용합니다.
        OAuthAttributes authAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        httpSession.setAttribute("user", new SessionUser(user));//세션에 사용자 정보를 저장하기 위한 dto 클래스

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                                        attributes.getAttributes(),
                                        attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail()).map(entity -> entity.update(attributes.getName(), attributes.getPicture())).orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}
