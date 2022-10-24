package com.ll.exam.ebooks.app.base.rq;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.security.dto.MemberContext;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequestScope
public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final MemberContext memberContext;
    @Getter
    private final Member member;

    public Rq(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;

        // 현재 로그인한 회원의 인증정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof MemberContext) {
            this.memberContext = (MemberContext) authentication.getPrincipal();
            this.member = memberContext.getMember();
        } else {
            this.memberContext = null;
            this.member = null;
        }
    }

    public String historyBack(String msg) {
        req.setAttribute("alertMsg", msg);
        return "common/js";
    }
}
