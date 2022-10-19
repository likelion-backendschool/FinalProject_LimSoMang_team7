package com.ll.exam.ebooks.app.base.initData;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.service.MemberService;
import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.post.service.PostService;

public interface InitDataBefore {
    default void before(MemberService memberService, PostService postService) {
        Member member1 = memberService.join("user1", "1234", "user1@test.com", "제노");
        Member member2 = memberService.join("user2", "1234", "user2@test.com", "해찬");
        Member member3 = memberService.join("user3", "1234", "user3@test.com", "마크");

        Post post1 = postService.save(member1, "test1", "## test1", "<h2>test1</h2>");
        Post post2 = postService.save(member2, "test2", "## test2", "<h2>test2</h2>");
        Post post3 = postService.save(member3, "test3", "## test3", "<h2>test3</h2>");
    }
}
