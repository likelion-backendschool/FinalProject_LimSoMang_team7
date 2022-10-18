package com.ll.exam.ebooks.app.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class MemberModifyPasswordRequestDto {
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
}
