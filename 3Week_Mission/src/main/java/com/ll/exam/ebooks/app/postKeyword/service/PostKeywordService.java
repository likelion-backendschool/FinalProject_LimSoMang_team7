package com.ll.exam.ebooks.app.postKeyword.service;

import com.ll.exam.ebooks.app.postKeyword.entity.PostKeyword;
import com.ll.exam.ebooks.app.postKeyword.repository.PostKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostKeywordService {
    private final PostKeywordRepository postKeywordRepository;

    // 키워드 저장
    public PostKeyword save(String content) {
        Optional<PostKeyword> optionalPostKeyword = postKeywordRepository.findByContent(content);

        // 1. 해당 키워드가(content)가 DB에 있으면 바로 리턴
        if (optionalPostKeyword.isPresent()) {
            return optionalPostKeyword.get();
        }

        // 2. 해당 키워드(content)가 DB에 없으면 저장
        PostKeyword postKeyword = PostKeyword
                .builder()
                .content(content)
                .build();

        postKeywordRepository.save(postKeyword);

        return postKeyword;
    }

    public PostKeyword findByContent(String content) {
        return postKeywordRepository.findByContent(content).orElse(null);
    }

    public List<PostKeyword> findByMemberId(Long authorId) {
        return postKeywordRepository.getQslAllByAuthorId(authorId);
    }

    public Optional<PostKeyword> findById(long id) {
        return postKeywordRepository.findById(id);
    }
}
