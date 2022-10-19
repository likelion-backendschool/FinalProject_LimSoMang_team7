package com.ll.exam.ebooks.app.security.service;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberSecurityService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> _member = memberRepository.findByUsername(username);

        if (_member.isEmpty()) {
            throw new UsernameNotFoundException("사용자가 존재하지 않습니다.");
        }

        Member member = _member.get();

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (member.getAuthLevel() > 6) {
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
        }

        if (member.getNickname() != null && member.getNickname().trim().length() > 0) {
            authorities.add(new SimpleGrantedAuthority("AUTHOR"));
        }

        authorities.add(new SimpleGrantedAuthority("MEMBER"));

        return new User(member.getUsername(), member.getPassword(), authorities);
    }
}
