package likelion14th.lte.user.domain;

import jakarta.persistence.*;
import likelion14th.lte.Entity.BaseEntity;
import likelion14th.lte.follow.domain.Follow;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String providerId;

    @Column(nullable = false)
    private String username;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Column(columnDefinition = "TEXT")
    private String profileImage;

    @Column(length = 16, nullable = false, unique = true)
    private String userTag;

    @Column(columnDefinition = "TEXT")
    private String s3ImageKey;

    @OneToMany(mappedBy = "toUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "fromUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followings = new ArrayList<>();


    @Builder
    public User (String providerId,  String username, String introduction,
                 String profileImage, String userTag, String s3ImageKey) {
        this.providerId = providerId;
        this.username = username;
        this.introduction = introduction;
        this.profileImage = profileImage;
        this.userTag = userTag;
        this.s3ImageKey = s3ImageKey;
        this.followers=new ArrayList<>();
        this.followings=new ArrayList<>();
    }
}
