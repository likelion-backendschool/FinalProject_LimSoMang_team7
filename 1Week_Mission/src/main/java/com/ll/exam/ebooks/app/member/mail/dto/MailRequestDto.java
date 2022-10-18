package com.ll.exam.ebooks.app.member.mail.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailRequestDto {
    private String to;
    private String subject;
    private String message;
}
