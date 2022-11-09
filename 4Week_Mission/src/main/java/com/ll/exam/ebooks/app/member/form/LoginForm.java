package com.ll.exam.ebooks.app.member.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginForm {
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
}
