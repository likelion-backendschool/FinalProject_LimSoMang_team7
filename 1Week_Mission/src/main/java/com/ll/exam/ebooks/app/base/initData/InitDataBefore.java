package com.ll.exam.ebooks.app.base.initData;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.service.MemberService;

public interface InitDataBefore {
    default void before(MemberService memberService) {
        Member member1 = memberService.join("user1", "1234", "user1@test.com", "제노");
        Member member2 = memberService.join("user2", "1234", "user2@test.com", "해찬");
        Member member3 = memberService.join("user3", "1234", "user3@test.com", "마크");
    }
}
