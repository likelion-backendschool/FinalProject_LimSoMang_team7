package com.ll.exam.ebooks.app.security.dto;

import com.ll.exam.ebooks.app.member.entity.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MemberContext extends User {
    private final Long id;
    private final String username;
    @Setter
    private String email;
    @Setter
    private String nickname;
    private final LocalDateTime createDate;
    @Setter
    private LocalDateTime modifyDate;

    public MemberContext(Member member, List<GrantedAuthority> authorities) {
        super(member.getUsername(), member.getPassword(), authorities);
        this.id = member.getId();
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.createDate = member.getCreateDate();
        this.modifyDate = member.getModifyDate();
    }

    public Member getMember() {
        return Member
                .builder()
                .id(id)
                .username(username)
                .email(email)
                .nickname(nickname)
                .createDate(createDate)
                .modifyDate(modifyDate)
                .build();
    }

    public String getName() {
        return getUsername();
    }
}
