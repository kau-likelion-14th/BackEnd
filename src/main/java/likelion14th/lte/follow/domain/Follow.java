package likelion14th.lte.follow.domain;

import jakarta.persistence.*;
import likelion14th.lte.Entity.BaseEntity;
import likelion14th.lte.user.domain.User;
import lombok.*;

/**
 * 팔로우 관계를 DB에 저장할 때 사용하는 엔티티 클래스입니다.
 * "fromUser가 toUser를 팔로우한다"는 관계 한 건을 테이블의 한 행으로 매핑합니다.
 * 엔티티는 DB 테이블과 1:1로 대응되는 자바 클래스를 의미합니다.
 *
 * @Entity : 이 클래스를 JPA 엔티티로 인식하며, DB 테이블과 매핑합니다.
 * @Getter : 각 필드에 대한 getter 메서드를 자동 생성합니다.
 * @AllArgsConstructor / @NoArgsConstructor : JPA가 엔티티를 생성·로딩할 때 사용하는 생성자입니다.
 * @Table(name="follow") : 매핑할 DB 테이블 이름을 "follow"로 지정합니다.
 */
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="follow")
public class Follow extends BaseEntity {
    /** @Id : 이 필드를 테이블의 기본키(Primary Key)로 사용합니다. */
    @Id
    /** @GeneratedValue : insert 시 DB가 id 값을 자동으로 생성합니다(예: AUTO_INCREMENT). */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** fromUser : 팔로우를 수행한 주체(누가 팔로우했는지)입니다. */
    @ManyToOne
    private User fromUser;

    /** toUser : 팔로우 대상(누구를 팔로우했는지)입니다. */
    @ManyToOne
    private User toUser;

    /** @Builder : 빌더 패턴으로 객체를 생성할 수 있게 합니다. Follow.builder().fromUser(...).toUser(...).build() 형태로 사용합니다. */
    @Builder
    public Follow( User fromUser, User toUser) {
        // 전달받은 fromUser, toUser를 이 엔티티의 필드에 저장합니다. (팔로우 관계 한 건을 나타내는 데이터가 됩니다)
        this.fromUser = fromUser;
        this.toUser = toUser;
    }
}
