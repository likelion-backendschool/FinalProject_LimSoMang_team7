package com.ll.exam.ebooks.app.rebate.controller;

import com.ll.exam.ebooks.app.rebate.entity.RebateOrderItem;
import com.ll.exam.ebooks.app.rebate.service.RebateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/adm/rebate")
@RequiredArgsConstructor
public class AdmRebateController {
    private final RebateService rebateService;

    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    @GetMapping("/makeData")
    public String showMakeData() {
        return "adm/rebate/makeData";
    }

    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    @PostMapping("/makeData")
    public String makeData(String yearMonth) {
        rebateService.makeDate(yearMonth);

        return "redirect:/adm/rebate/rebateOrderItemList";
    }

    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    @GetMapping("/rebateOrderItemList")
    public String showRebateOrderItemList(String yearMonth, Model model) {
        if (yearMonth == null) {
            yearMonth = "2022-11";
        }

        List<RebateOrderItem> items = rebateService.findRebateOrderItemsByPayDateIn(yearMonth);

        model.addAttribute("items", items);

        return "adm/rebate/rebateOrderItemList";
    }
}
