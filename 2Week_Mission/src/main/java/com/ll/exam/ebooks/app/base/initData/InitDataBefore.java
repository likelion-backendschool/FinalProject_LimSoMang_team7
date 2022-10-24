package com.ll.exam.ebooks.app.base.initData;

import com.ll.exam.ebooks.app.member.form.JoinForm;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.member.service.MemberService;
import com.ll.exam.ebooks.app.post.form.PostForm;
import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.post.service.PostService;

public interface InitDataBefore {
    default void before(MemberService memberService, PostService postService) {
        JoinForm joinForm = new JoinForm("user1", "1234", "1234", "user1@test.com", "제노");
        Member member1 = memberService.adminJoin(joinForm);
        joinForm = new JoinForm("user2", "1234", "1234", "user2@test.com", "해찬");
        Member member2 = memberService.join(joinForm);
        joinForm = new JoinForm("user3", "1234", "1234", "user3@test.com", "마크");
        Member member3 = memberService.join(joinForm);
        joinForm = new JoinForm("user4", "1234", "1234", "user4@test.com", "");
        Member member4 = memberService.join(joinForm);

        PostForm postForm = new PostForm("test1", "## test1", "<h2>test1</h2>", "#test #user1");
        Post post1 = postService.write(member1, postForm);
        postForm = new PostForm("test2", "## test2", "<h2>test2</h2>", "#test #user2");
        Post post2 = postService.write(member2, postForm);
        postForm = new PostForm("test3", "## test3", "<h2>test3</h2>", "#test #user3");
        Post post3 = postService.write(member3, postForm);
    }
}
