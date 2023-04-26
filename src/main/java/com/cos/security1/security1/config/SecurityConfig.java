package com.cos.security1.security1.config;


import com.cos.security1.security1.config.auth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//구글 로그인이 완료된 뒤의 후처리가 필요함. 1. 코드받기(인증), 2.엑세스토큰(권한), 3.사용자프로필 정보를 가져오고 4-1. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함
//4-2 구글은 (이메일, 전화번호, 이름, 아이디)를 제공함 하지만  쇼핑몰 -> 집주소, 고객의 등급(vip, 일반등급)가 추가적으로 필요함
// 구글 로그인이 완료되면 코드를 받는게 아니라 AcessToken +  사용자 프로필 정보를 한방에 받는다.
//그래서 OauthClient라이브러리를 쓰면 편리하다.
@Configuration // IoC 빈(bean)을 등록
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 됨
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 어노테이션 활성화, preAuthorize 어노테이션 활성화
public class SecurityConfig {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**").access("hasRole('MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ADMIN')")
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login")//login 주소가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행함.
                .defaultSuccessUrl("/")
                .and()
                .oauth2Login()
                .loginPage("/loginForm")
                .userInfoEndpoint()
                .userService(principalOauth2UserService);




        return http.build();
    }
}