package com.ll.exam.ebooks.app.post.repository;

import com.ll.exam.ebooks.app.member.entity.Member;
import com.ll.exam.ebooks.app.post.entity.Post;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ll.exam.ebooks.app.post.entity.QPost.post;
import static com.ll.exam.ebooks.app.postHashTag.entity.QPostHashTag.postHashTag;
import static com.ll.exam.ebooks.app.postKeyword.entity.QPostKeyword.postKeyword;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Post> getQslPostsOrderByIdDesc() {
        return jpaQueryFactory
                .select(post)
                .from(post)
                .orderBy(post.id.desc())
                .fetch();
    }

    @Override
    public List<Post> mainListSearchQsl(String kwType, String kw) {
        // 1. 전체 조회(키워드가 없거나 검색 타입이 hashTag가 아닌 경우)
        JPAQuery<Post> jpaQuery = jpaQueryFactory
                .select(post)
                .distinct()
                .from(post);

        // 2. 키워드 기반 검색(키워드가 존재하거나 키워드 타입이 hashTag인 경우)
        if (!kw.equals("")) {
            if (kwType.equals("hashTag")) {
                jpaQuery
                        .innerJoin(postHashTag)
                        .on(post.eq(postHashTag.post))
                        .innerJoin(postKeyword)
                        .on(postKeyword.eq(postHashTag.postKeyword))
                        .where(postKeyword.content.eq(kw));
            }
        }
        jpaQuery.orderBy(post.id.desc())
                .limit(100);

        return jpaQuery.fetch();
    }

    @Override
    public List<Post> searchQsl(Member author, String kwType, String kw) {
        // 1. 전체 조회(키워드가 없거나 검색 타입이 hashTag가 아닌 경우)
        JPAQuery<Post> jpaQuery = jpaQueryFactory
                .select(post)
                .where(post.author.id.eq(author.getId()))
                .distinct()
                .from(post);

        // 2. 키워드 기반 검색(키워드가 존재하거나 키워드 타입이 hashTag인 경우)
        if (!kw.equals("")) {
            if (kwType.equals("hashTag")) {
                jpaQuery
                        .innerJoin(postHashTag)
                        .on(post.eq(postHashTag.post))
                        .innerJoin(postKeyword)
                        .on(postKeyword.eq(postHashTag.postKeyword))
                        .where(postKeyword.content.eq(kw));
            }
        }
        jpaQuery.orderBy(post.id.desc());

        return jpaQuery.fetch();
    }
}
