package com.ll.exam.ebooks.app.member.service;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.exception.AlreadyJoinException;
import com.ll.exam.ebooks.app.member.exception.DuplicateNicknameException;
import com.ll.exam.ebooks.app.member.mail.dto.MailRequestDto;
import com.ll.exam.ebooks.app.member.mail.service.MailService;
import com.ll.exam.ebooks.app.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;


    @Transactional
    public Member join(String username, String password, String email, String nickname) {
        if (memberRepository.findByUsername(username).isPresent()) {
            throw new AlreadyJoinException();
        }

        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new DuplicateNicknameException();
        }

        int authLevel = 3;

        Member member = Member
                .builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .nickname(nickname)
                .authLevel(authLevel)
                .build();

        memberRepository.save(member);

        return member;
    }

    @Transactional
    public void mailSend(Member member, String subject, String message) {
        MailRequestDto mailDto = MailRequestDto
                .builder()
                .to(member.getEmail())
                .subject(subject)
                .message(message)
                .build();

        mailService.mailSend(mailDto);
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
    public void setTempPassword(Member member, String tempPassword) {
        member.setPassword(passwordEncoder.encode(tempPassword));

        memberRepository.save(member);
    }

    @Transactional
    public void modify(String username, String email, String nickname) {
        Member member = memberRepository.findByUsername(username).get();

        if (memberRepository.findByEmail(email).isEmpty()) {
            member.setEmail(email);
        }

        if (memberRepository.findByNickname(nickname).isEmpty()) {
            member.setNickname(nickname);
        }

        memberRepository.save(member);
    }

    public void modifyPassword(String username, String password) {
        Member member = memberRepository.findByUsername(username).get();

        member.setPassword(passwordEncoder.encode(password));

        memberRepository.save(member);
    }
}