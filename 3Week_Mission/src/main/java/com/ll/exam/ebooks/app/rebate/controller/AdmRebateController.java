package com.ll.exam.ebooks.app.rebate.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/adm/rebate")
public class AdmRebateController {
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    @GetMapping("/makeData")
    public String showMakeData() {
        return "adm/rebate/makeData";
    }
}
