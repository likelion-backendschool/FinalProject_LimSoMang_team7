package com.ll.exam.ebooks.app.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class PostForm {
    @NotEmpty
    private String subject;

    @NotEmpty
    private String content;

    @NotEmpty
    private String contentHtml;

    @NotEmpty
    private String hashTags;
}
