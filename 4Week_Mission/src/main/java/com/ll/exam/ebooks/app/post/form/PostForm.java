package com.ll.exam.ebooks.app.post.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
public class PostForm {
    @NotEmpty
    private String subject;

    @NotEmpty
    private String content;

    @NotEmpty
    private String contentHtml;

    @NotEmpty
    private String postHashTags;
}
