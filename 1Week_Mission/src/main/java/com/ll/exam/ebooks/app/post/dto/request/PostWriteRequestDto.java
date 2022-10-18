package com.ll.exam.ebooks.app.post.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class PostWriteRequestDto {
    @NotEmpty
    private String subject;

    @NotEmpty
    private String content;

    @NotEmpty
    private String contentHtml;

    private String keywords;
}
