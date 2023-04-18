package com.cos.security1.security1.repository;

import com.cos.security1.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

//crud 함수를 JpaRepository가 들고있음
// @Repository라는 어노테이션이 없어도 ioc된다. 이유는 JPARepository를 상속했기 때문에
public interface UserRepository extends JpaRepository<User, Integer> {

}
