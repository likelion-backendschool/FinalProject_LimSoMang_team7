package com.ll.exam.ebooks.app.mail.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;

@Component
public class MailHandler {
    private final JavaMailSender javaMailSender;
    private final MimeMessage mimeMessage;
    private final MimeMessageHelper mimeMessageHelper;

    public MailHandler(JavaMailSender javaMailSender) throws MessagingException {
        this.javaMailSender = javaMailSender;
        mimeMessage = javaMailSender.createMimeMessage();
        mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    }

    public void setFrom(String fromAddress) throws MessagingException {
        mimeMessageHelper.setFrom(fromAddress);
    }

    public void setTo(String email) throws MessagingException {
        mimeMessageHelper.setTo(email);
    }

    public void setSubject(String subject) throws MessagingException {
        mimeMessageHelper.setSubject(subject);
    }

    public void setText(String text, boolean useHtml) throws MessagingException {
        mimeMessageHelper.setText(text, useHtml);
    }

    public void setAttach(String displayFileName, FileDataSource file) throws MessagingException {
        mimeMessageHelper.addAttachment(displayFileName, file);
    }

    public void setInline(String contentId, String pathToInline) throws MessagingException, IOException {
        File file = new ClassPathResource(pathToInline).getFile();
        FileSystemResource fileSystemResource = new FileSystemResource(file);

        mimeMessageHelper.addInline(contentId, fileSystemResource);
    }

    public void send() {
        try {
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
