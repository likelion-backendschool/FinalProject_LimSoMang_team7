package com.ll.exam.ebooks.app.postKeyword.repository;

import com.ll.exam.ebooks.app.postKeyword.entity.PostKeyword;

import java.util.List;

public interface PostKeywordRepositoryCustom {
    List<PostKeyword> getQslAllByAuthorId(Long authorId);
}
