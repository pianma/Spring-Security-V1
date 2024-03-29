package com.cos.security1.security1.config.oauth;

import com.cos.security1.security1.config.auth.PrincipalDetails;
import com.cos.security1.security1.config.oauth.provider.FacebookUserInfo;
import com.cos.security1.security1.config.oauth.provider.GoogleUserInfo;
import com.cos.security1.security1.config.oauth.provider.NaverUserInfo;
import com.cos.security1.security1.config.oauth.provider.OAuth2UserInfo;
import com.cos.security1.security1.model.User;
import com.cos.security1.security1.repository.UserRepository;
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


    @Autowired
    private UserRepository userRepository;



    //구글로부터 받는 userRequest 데이터에 대한 후처리되는 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        System.out.println("userRequest :" + userRequest.getClientRegistration()); //registrationId로 어떤 Oauth로 로그인 했는지 확인 가능
        System.out.println("userRequest :" + userRequest.getAccessToken().getTokenValue());

        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 구글 로그인 버튼 클릭 -> 구글 로그인창 -> 로그인을 완료 -> code를 리턴(Oauth-client 라이브러리)->AcessToken 요청
        // userRequest 정보 -> loadUser함수 -> 구글로부터 회원 프로필 받아준다.
        System.out.println("userRequest :" + oAuth2User.getAttributes());


        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("구글 로그인 요청");

            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());

        }else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
            System.out.println("페이스북 로그인 요청");

            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());

        }else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            System.out.println("네이버 로그인 요청");

            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));

        }else{
            System.out.println("구글과 페이스북, 네이버만 지원");
        }


        String provider = oAuth2UserInfo.getProvider(); // google
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId; // google_87464687684618
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        if(userEntity ==null){
            System.out.println("oauth 로그인 최초");
            userEntity = User.builder()
                    .username(username)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }else{
            System.out.println("이미 가입된 아이디");
        }

        //회원 가입을 강제로 진행해볼 예정
        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
