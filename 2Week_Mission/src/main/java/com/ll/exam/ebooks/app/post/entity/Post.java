package com.ll.exam.ebooks.app.post.entity;

import com.ll.exam.ebooks.app.base.entity.BaseEntity;
import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.postHashTag.entity.PostHashTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Post extends BaseEntity {
    private String subject;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(columnDefinition = "LONGTEXT")
    private String contentHtml;

    @ManyToOne(fetch = LAZY)
    private Member author;

    @OneToMany(mappedBy = "post", cascade = ALL)
    private List<PostHashTag> hashTags = new ArrayList<>();

    public String getHashTagString() {
        if (hashTags.isEmpty()) {
            return "";
        }

        return "#" + hashTags
                .stream()
                .map(hashTag -> hashTag.getPostKeyword().getContent())
                .sorted()
                .collect(Collectors.joining(" #"))
                .trim();
    }
}
