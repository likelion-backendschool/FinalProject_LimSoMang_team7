package com.ll.exam.ebooks.app.product.form;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ProductForm {
    @NotBlank
    private String subject;

    @NotNull
    private int price;

    @NotNull
    private Long postKeywordId;

    @NotBlank
    private String productHashTags;
}
