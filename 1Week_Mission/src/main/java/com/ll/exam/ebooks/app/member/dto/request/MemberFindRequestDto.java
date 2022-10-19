package com.ll.exam.ebooks.app.member.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class MemberFindRequestDto {
    private String username;

    @NotEmpty
    private String email;
}