package com.ll.exam.ebooks.app.postKeyword.service;

import com.ll.exam.ebooks.app.postKeyword.entity.PostKeyword;
import com.ll.exam.ebooks.app.postKeyword.repository.PostKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostKeywordService {
    private final PostKeywordRepository postKeywordRepository;

    public PostKeyword save(String content) {
        Optional<PostKeyword> optionalPostKeyword = postKeywordRepository.findByContent(content);

        if (optionalPostKeyword.isPresent()) {
            return optionalPostKeyword.get();
        }

        PostKeyword postKeyword = PostKeyword
                .builder()
                .content(content)
                .build();

        postKeywordRepository.save(postKeyword);

        return postKeyword;
    }
}
