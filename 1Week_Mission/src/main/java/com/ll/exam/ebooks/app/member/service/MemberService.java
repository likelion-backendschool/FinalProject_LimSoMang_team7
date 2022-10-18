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

import java.util.NoSuchElementException;

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

        int authLevel = checkAuthLevel(nickname);

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

    public int checkAuthLevel(String nickname) {
        int authLevel = 3;

        if (nickname != null && nickname.trim().length() > 0) {
            authLevel = 4;
        }

        return authLevel;
    }

    public void mailSend(Member member) {
        String subject = "🦁멋북스 회원가입을 진심으로 환영합니다.";
        String message = "멋쟁이사자처럼, 책 많이 읽고 멋쟁이 되세요. 😎";

        MailRequestDto mailDto = MailRequestDto
                .builder()
                .to(member.getEmail())
                .subject(subject)
                .message(message)
                .build();

        mailService.mailSend(mailDto);
    }

    public Member findByEmail(String email) {
        if (memberRepository.findByEmail(email).isEmpty()) {
            throw new NoSuchElementException();
        }

        Member member = memberRepository.findByEmail(email).get();

        return member;
    }
}
