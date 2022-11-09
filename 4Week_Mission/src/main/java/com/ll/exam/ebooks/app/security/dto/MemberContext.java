package com.ll.exam.ebooks.app.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ll.exam.ebooks.app.member.entity.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@JsonIgnoreProperties({"id", "createDate", "modifyDate", "username", "email", "nickname"})
public class MemberContext extends User {
    private final Long id;
    private final String username;
    private final String email;
    private final String nickname;
    private final LocalDateTime createDate;
    private final LocalDateTime modifyDate;

    public MemberContext(Member member) {
        super(member.getUsername(), "", member.getAuthorities());

        id = member.getId();
        username = member.getUsername();
        email = member.getEmail();
        nickname = member.getNickname();
        createDate = member.getCreateDate();
        modifyDate = member.getModifyDate();
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

    public boolean hasAuthority(String authorityName) {
        return getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authorityName));
    }
}
