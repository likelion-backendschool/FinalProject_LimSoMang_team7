package com.ll.exam.ebooks.app.postTag.entity;

import com.ll.exam.ebooks.app.base.entity.BaseEntity;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.postKeyword.entity.PostKeyword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class PostTag extends BaseEntity {
    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    private Post post;

    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    private Member member;

    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    private PostKeyword postKeyword;
}
