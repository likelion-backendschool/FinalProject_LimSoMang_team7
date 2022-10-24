package com.ll.exam.ebooks.app.member.service;

import com.ll.exam.ebooks.app.member.form.JoinForm;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.mail.service.MailService;
import com.ll.exam.ebooks.app.member.exception.AlreadyJoinException;
import com.ll.exam.ebooks.app.member.exception.MemberNotFoundException;
import com.ll.exam.ebooks.app.member.repository.MemberRepository;
import com.ll.exam.ebooks.app.security.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;


    @Transactional
    public Member join(JoinForm joinForm) {
        if (memberRepository.findByUsername(joinForm.getUsername()).isPresent()) {
            throw new AlreadyJoinException();
        }

        Member member = Member
                .builder()
                .username(joinForm.getUsername())
                .password(passwordEncoder.encode(joinForm.getPassword()))
                .email(joinForm.getEmail())
                .nickname(joinForm.getNickname())
                .authLevel(3)
                .build();

        memberRepository.save(member);

        mailService.joinMailSend(member);

        return member;
    }

    @Transactional
    public Member adminJoin(JoinForm joinForm) {
        if (memberRepository.findByUsername(joinForm.getUsername()).isPresent()) {
            throw new AlreadyJoinException();
        }

        Member member = Member
                .builder()
                .username(joinForm.getUsername())
                .password(passwordEncoder.encode(joinForm.getPassword()))
                .email(joinForm.getEmail())
                .nickname(joinForm.getNickname())
                .authLevel(7)
                .build();

        memberRepository.save(member);

        mailService.joinMailSend(member);

        return member;
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElse(null);
    }

    public Member findByUsernameAndEmail(String username, String email) {
        return memberRepository.findByUsernameAndEmail(username, email).orElse(null);
    }

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username).orElse(null);
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
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new MemberNotFoundException("회원이 존재하지 않습니다."));

        member.setEmail(email);
        member.setNickname(nickname);
    }

    @Transactional
    public boolean modifyPassword(Member member, String oldPassword, String password) {
        Optional<Member> opMember = memberRepository.findByUsername(member.getUsername());

        if (passwordEncoder.matches(oldPassword, opMember.get().getPassword()) == false) {
            return false;
        }

        opMember.get().setPassword(passwordEncoder.encode(password));

        return true;
    }

    @Transactional
    public boolean beAuthor(Member member, String nickname) {
        Optional<Member> opMember = memberRepository.findByNickname(nickname);

        if (opMember.isPresent()) {
            return false;
        }

        opMember = memberRepository.findById(member.getId());

        opMember.get().setNickname(nickname);
        forceAuthentication(opMember.get());

        return true;
    }

    private void forceAuthentication(Member member) {
        MemberContext memberContext = new MemberContext(member, member.getAuthorities());

        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        memberContext,
                        member.getPassword(),
                        memberContext.getAuthorities()
                );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    public Member findByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).orElse(null);
    }
}
