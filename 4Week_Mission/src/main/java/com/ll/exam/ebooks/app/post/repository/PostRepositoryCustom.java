package com.ll.exam.ebooks.app.post.repository;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.post.entity.Post;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> getQslPostsOrderByIdDesc();

    List<Post> searchQsl(Member author, String kwType, String kw);

    List<Post> mainListSearchQsl(String kwType, String kw);
}
