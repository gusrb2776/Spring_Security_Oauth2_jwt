package com.cos.security1.repository;

import com.cos.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// 내 <entity, primaryKey 타입>
// CRUD는 기본적으로 JpaRepository가 들고있음
// @Repository란 어노테이션 없어도 IoC됨. JpaRepository가 갖고있음.
public interface UserRepository extends JpaRepository<User, Integer> {

    // findBy까지 적는건 규칙 -> Username은 문법
    // select * from user where username = ?  이 나오게됨.
    public User findByUsername(String username);
}
