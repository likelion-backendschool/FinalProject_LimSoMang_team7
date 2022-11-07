package com.ll.exam.ebooks.app.postHashTag.service;


import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.post.service.PostService;
import com.ll.exam.ebooks.app.postHashTag.entity.PostHashTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PostHashTagServiceTest {

    @Autowired
    private PostHashTagService postHashTagService;

    @Autowired
    private PostService postService;

    @Test
    @DisplayName("입력된 문자열에서 해시태그를 추출해서 4번 게시글에 등록하기")
    void apply() {
        // Given
        Post post = postService.findById(4L);
        String keywords = "#런쥔  # 천러 #재민 #천러";

        // When
        postHashTagService.apply(post, keywords);

        // Then
        List<PostHashTag> hashTags = postHashTagService.findByPostId(4L);
        List<String> hashTagsString = hashTags.stream()
                .map(tag -> tag.getPostKeyword().getContent())
                .collect(Collectors.toList());

        assertThat(hashTags.size()).isGreaterThan(0);
        assertEquals(hashTagsString.get(0), "런쥔");
        assertEquals(hashTagsString.get(1), "천러");
        assertEquals(hashTagsString.get(2), "재민");

    }


    @Test
    @DisplayName("4번 게시글에 해시태그 키워드 3개 등록하기")
    void save() {
        // Given
        Post post = postService.findById(4L);
        String keyword1 = "태용";
        String keyword2 = "쟈니";
        String keyword3 = "재현";
        String keyword4 = "쟈니";

        // When
        postHashTagService.save(post, keyword1);
        postHashTagService.save(post, keyword2);
        postHashTagService.save(post, keyword3);
        postHashTagService.save(post, keyword4);

        // Then
        List<PostHashTag> hashTags = postHashTagService.findByPostId(4L);
        List<String> hashTagsString = hashTags.stream()
                .map(tag -> tag.getPostKeyword().getContent())
                .collect(Collectors.toList());

        assertThat(hashTags.size()).isGreaterThan(3);
        assertEquals(hashTagsString.get(0), "test");
        assertEquals(hashTagsString.get(1), "user2");
        assertEquals(hashTagsString.get(2), "태용");
        assertEquals(hashTagsString.get(3), "쟈니");
        assertEquals(hashTagsString.get(4), "재현");
    }

}