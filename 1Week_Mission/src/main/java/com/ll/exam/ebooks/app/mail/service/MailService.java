package com.ll.exam.ebooks.app.mail.service;

import com.ll.exam.ebooks.app.mail.handler.MailHandler;
import com.ll.exam.ebooks.app.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.IOException;


@Service
@Transactional
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;

    @Async
    public void joinMailSend(Member member) {
        try {
            MailHandler mailHandler = new MailHandler(javaMailSender);
            mailHandler.setTo(member.getEmail());
            mailHandler.setSubject("🦁멋북스 회원가입을 진심으로 환영합니다.");

            String htmlContent = "<h2>멋쟁이사자처럼, 책 많이 읽고 멋쟁이 되세요. 😎</h2> <img src='cid:welcome-img' width='50%' height='50%'>";
            mailHandler.setText(htmlContent, true);

            String pathFile = "static/resource/image/welcome.jpg";
            mailHandler.setInline("welcome-img", pathFile);

            mailHandler.send();
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    public void tempPasswordMailSend(Member member, String tempPassword) {
        try {
            MailHandler mailHandler = new MailHandler(javaMailSender);
            mailHandler.setTo(member.getEmail());
            mailHandler.setSubject("🦁 %s 님 멋북스 임시 비밀번호가 생성되었습니다.".formatted(member.getUsername()));

            mailHandler.setText(tempPassword, true);

            mailHandler.send();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
