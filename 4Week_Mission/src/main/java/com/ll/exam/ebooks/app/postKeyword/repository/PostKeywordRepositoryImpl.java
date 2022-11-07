package com.ll.exam.ebooks.app.postKeyword.repository;

import com.ll.exam.ebooks.app.postKeyword.entity.PostKeyword;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.ll.exam.ebooks.app.postHashTag.entity.QPostHashTag.postHashTag;
import static com.ll.exam.ebooks.app.postKeyword.entity.QPostKeyword.postKeyword;

@RequiredArgsConstructor
public class PostKeywordRepositoryImpl implements PostKeywordRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PostKeyword> getQslAllByAuthorId(Long authorId) {
        List<Tuple> fetch = jpaQueryFactory
                .select(postKeyword, postHashTag.count())
                .from(postKeyword)
                .innerJoin(postHashTag)
                .on(postKeyword.eq(postHashTag.postKeyword))
                .where(postHashTag.author.id.eq(authorId))
                .orderBy(postHashTag.post.id.desc())
                .groupBy(postKeyword.id)
                .fetch();

        return fetch.stream()
                .map(tuple -> {
                    PostKeyword _postKeyword = tuple.get(postKeyword);
                    Long postHashTagsCount = tuple.get(postHashTag.count());

                    _postKeyword.setPostHashTagsCount(postHashTagsCount);

                    return _postKeyword;
                })
                .collect(Collectors.toList());
    }
}
