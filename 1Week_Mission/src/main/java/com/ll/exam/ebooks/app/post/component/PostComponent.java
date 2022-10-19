package com.ll.exam.ebooks.app.post.component;

import com.ll.exam.ebooks.app.post.dto.response.ResponseAuthor;
import com.ll.exam.ebooks.app.post.dto.response.ResponsePost;
import com.ll.exam.ebooks.app.post.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostComponent {

    public ResponsePost getResponsePost(Post post) {
        ResponsePost responsePost = new ResponsePost().fromEntity(post);

        if (post.getAuthor() != null) {
            responsePost.setAuthor(
                    new ResponseAuthor().fromEntity(post.getAuthor())
            );
        }

        return responsePost;
    }

}
