package com.ll.exam.ebooks.app.post.dto.response;

import com.ll.exam.ebooks.app.member.entity.Member;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseAuthor {
    private long id;

    private String username;

    private String email;

    private String nickname;

    public static ResponseAuthor fromEntity(Member author) {
        return ResponseAuthor.builder()
                .id(author.getId())
                .username(author.getUsername())
                .email(author.getEmail())
                .nickname(author.getNickname())
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .id(id)
                .username(username)
                .email(email)
                .nickname(nickname)
                .build();
    }

    @Builder
    public ResponseAuthor(long id, String username, String email, String nickname) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nickname = nickname;
    }
}
