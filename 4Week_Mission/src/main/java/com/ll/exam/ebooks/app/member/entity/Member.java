package com.ll.exam.ebooks.app.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.exam.ebooks.app.base.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Member extends BaseEntity {
    @Column(unique = true)
    private String username;

    @JsonIgnore
    private String password;

    @Column(unique = true)
    private String email;

    private String nickname;

    private int authLevel;

    private long restCash; // 예치금

    public String getName() {
        if (nickname == null) {
            return username;
        }
        return nickname;
    }

    public Member(long id) {
        super(id);
    }

    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("MEMBER"));

        if (StringUtils.hasText(nickname)) {
            authorities.add(new SimpleGrantedAuthority("AUTHOR"));
        }

        if (authLevel == 7) {
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
        }

        return authorities;
    }
}
