package com.ll.exam.ebooks.app.member.repository;

import com.ll.exam.ebooks.app.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);

    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByEmail(String email);
}
