package likelion14th.lte.user.domain;

import jakarta.persistence.*;
import likelion14th.lte.Entity.BaseEntity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String providerId;

    @Column(nullable = false)
    private String username;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Column(columnDefinition = "TEXT")
    private String profileImage;

    @Column(length = 16)
    private String userTag;

    @Column(columnDefinition = "TEXT")
    private String s3ImageKey;

    @Builder
    public User (String providerId,  String username, String introduction,
                 String profileImage, String userTag, String s3ImageKey) {
        this.providerId = providerId;
        this.username = username;
        this.introduction = introduction;
        this.profileImage = profileImage;
        this.userTag = userTag;
        this.s3ImageKey = s3ImageKey;
    }
}
