package com.deerear.app.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
@Table(name = "members")
public class Member extends ModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    private String nickname;

    @Column(name = "profile_img_url")
    private String profileImgUrl;

    @Column(name = "refresh_token")
    private String refreshToken;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한 목록을 반환하는 로직을 구현
        return List.of(() -> "ROLE_USER");
    }
}