package com.ll.exam.ebooks.app.member.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModifyPasswordForm {
    private String oldPassword;
    private String password;
}
