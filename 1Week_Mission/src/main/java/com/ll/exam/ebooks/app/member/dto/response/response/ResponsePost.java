package com.ll.exam.ebooks.app.member.dto.response.response;

import com.ll.exam.ebooks.app.member.dto.response.ResponseAuthor;
import com.ll.exam.ebooks.app.post.dto.entity.Post;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ResponsePost {
    private long id;

    private String subject;

    private String content;

    private String contentHtml;

    private ResponseAuthor author;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    public ResponsePost fromEntity(Post p) {
        return ResponsePost.builder()
                .id(p.getId())
                .subject(p.getSubject())
                .content(p.getContent())
                .contentHtml(p.getContentHtml())
                .createDate(p.getCreateDate())
                .modifyDate(p.getModifyDate())
                .build();
    }

    public Post toEntity() {
        return Post.builder()
                .id(id)
                .subject(subject)
                .content(content)
                .contentHtml(contentHtml)
                .author(author.toEntity())
                .createDate(createDate)
                .modifyDate(modifyDate)
                .build();
    }

    @Builder
    public ResponsePost(long id, String subject, String content, String contentHtml, LocalDateTime createDate, LocalDateTime modifyDate) {
        this.id = id;
        this.subject = subject;
        this.content = content;
        this.contentHtml = contentHtml;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
    }
}
