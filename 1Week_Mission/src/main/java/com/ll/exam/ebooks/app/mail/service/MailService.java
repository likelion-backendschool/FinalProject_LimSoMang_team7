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
            mailHandler.setSubject("π¦λ©λΆμ€ νμκ°μμ μ§μ¬μΌλ‘ νμν©λλ€.");

            String htmlContent = "<h2>λ©μμ΄μ¬μμ²λΌ, μ± λ§μ΄ μ½κ³  λ©μμ΄ λμΈμ. π</h2> <img src='cid:welcome-img' width='50%' height='50%'>";
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
            mailHandler.setSubject("π¦ %s λ λ©λΆμ€ μμ λΉλ°λ²νΈκ° μμ±λμμ΅λλ€.".formatted(member.getUsername()));

            mailHandler.setText(tempPassword, true);

            mailHandler.send();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
