package likelion14th.lte.follow.domain;

import jakarta.persistence.*;
import likelion14th.lte.Entity.BaseEntity;
import likelion14th.lte.user.domain.User;
import lombok.*;

/**
 *  팔로우 관계를 나타내는 JPA 엔티티.
 * "fromUser가 toUser를 팔로우한다"는 관계를 DB에 저장합니다.
 *
 * @Entity: JPA 엔티티로 인식, DB 테이블과 매핑.
 * @Getter: Lombok - 모든 필드에 대한 getter 자동 생성.
 * @AllArgsConstructor(access = PROTECTED): 모든 필드 생성자(protected). JPA 프록시 등에서 사용.
 * @NoArgsConstructor(access = PROTECTED): 인자 없는 생성자(protected). JPA 스펙 요구.
 * @Table(name="follow"): 매핑할 테이블 이름. 생략 시 클래스명(Follow)을 snake_case로 사용.
 */
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="follow")
public class Follow extends BaseEntity {
    /**  @Id: JPA에서 이 필드를 테이블의 기본키(PK)로 사용함 */
    @Id //  @GeneratedValue(strategy = IDENTITY): DB가 insert 시 자동으로 값을 생성(MySQL AUTO_INCREMENT 등)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**  @ManyToOne: N:1 관계. 여러 Follow 레코드가 하나의 User를 참조할 수 있음 (팔로우를 건 유저) */
    @ManyToOne
    private User fromUser;

    /**  @ManyToOne: 팔로우 대상 유저. 여러 Follow 레코드가 하나의 User를 참조 가능 */
    @ManyToOne
    private User toUser;

    /**  @Builder: 빌더 패턴으로 객체 생성 가능. Follow.builder().fromUser(...).toUser(...).build() */
    @Builder
    public Follow( User fromUser, User toUser) {
        this.fromUser = fromUser;
        this.toUser = toUser;
    }
}
