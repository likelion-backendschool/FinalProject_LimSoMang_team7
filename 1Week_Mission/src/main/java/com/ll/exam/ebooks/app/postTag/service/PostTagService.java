package com.ll.exam.ebooks.app.postTag.service;

import com.ll.exam.ebooks.app.post.entity.Post;
import com.ll.exam.ebooks.app.postKeyword.entity.PostKeyword;
import com.ll.exam.ebooks.app.postTag.entity.PostTag;
import com.ll.exam.ebooks.app.postTag.repository.PostTagRepository;
import com.ll.exam.ebooks.app.postKeyword.service.PostKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostTagService {
    private final PostTagRepository postTagRepository;
    private final PostKeywordService postKeywordService;

    public void applyPostTags(Post post, String postTags) {
        List<String> postTagsContents = Arrays.stream(postTags.split("#"))
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .collect(Collectors.toList());

        postTagsContents.forEach(postKeywordContent -> {
            savePostTag(post, postKeywordContent);
        });
    }

    public PostTag savePostTag(Post post, String postTagContent) {
        PostKeyword postKeyword = postKeywordService.save(postTagContent);

        Optional<PostTag> optionalPostTag = postTagRepository.findByPostIdAndPostKeywordId(post.getId(), postKeyword.getId());

        if (optionalPostTag.isPresent()) {
            return optionalPostTag.get();
        }

        PostTag postTag = PostTag
                .builder()
                .post(post)
                .member(post.getAuthor())
                .postKeyword(postKeyword)
                .build();

        postTagRepository.save(postTag);

        return postTag;
    }

}
