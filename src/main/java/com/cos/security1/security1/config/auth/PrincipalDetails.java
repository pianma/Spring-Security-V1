package com.cos.security1.security1.config.auth;

//시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행
// 로그인을 진행이 완료가 되면 시큐리티 session을 만들어 줍니다.(Security ContextHolder)
// Security Session -> Authentication -> UserDetails

import com.cos.security1.security1.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class PrincipalDetails implements UserDetails {

    private User user;

    public PrincipalDetails(User user){
        this.user = user;
    }


    public PrincipalDetails() {
        super();
    }

    //해당 user의 권한을 리턴하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {

        //우리 사이트에서 1년동안 회원이 로그인을 안하면 휴면 계정으로 하기로 함.
        //현재시간 - 로그인시간 => 1년초과하면 return false;

        return true;
    }
}
