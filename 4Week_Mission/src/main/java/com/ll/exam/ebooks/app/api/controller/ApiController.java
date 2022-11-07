package com.ll.exam.ebooks.app.api.controller;

import com.ll.exam.ebooks.app.member.form.LoginForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ApiController {

    @PostMapping("/member/login")
    public ResponseEntity<String> login(@RequestBody LoginForm loginForm) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authentication", "JWT token");

        String body = "username : %s, password : %s".formatted(loginForm.getUsername(), loginForm.getPassword());

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

}
