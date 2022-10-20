package com.ll.exam.ebooks.app.member.service;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.mail.service.MailService;
import com.ll.exam.ebooks.app.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;


    @Transactional
    public Member join(String username, String password, String email, String nickname) {
        Member member = Member
                .builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .nickname(nickname)
                .authLevel(3)
                .build();

        memberRepository.save(member);

        mailService.joinMailSend(member);

        return member;
    }

    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Member> findByUsernameAndEmail(String username, String email) {
        return memberRepository.findByUsernameAndEmail(username, email);
    }

    @Transactional(readOnly = true)
    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    @Transactional
    public void setTempPassword(Member member) {
        UUID uuid = UUID.randomUUID();
        String tempPassword = uuid.toString().substring(0, 6);

        member.setPassword(passwordEncoder.encode(tempPassword));

        memberRepository.save(member);

        mailService.tempPasswordMailSend(member, tempPassword);
    }

    @Transactional
    public void modify(String username, String email, String nickname) {
        Member member = memberRepository.findByUsername(username).get();

        member.setEmail(email);
        member.setNickname(nickname);
    }

    @Transactional
    public void modifyPassword(String username, String password) {
        Member member = memberRepository.findByUsername(username).get();

        member.setPassword(passwordEncoder.encode(password));

        memberRepository.save(member);
    }
}
