package likelion14th.lte.category.domain;

import jakarta.persistence.*;
import likelion14th.lte.Entity.BaseEntity;
import likelion14th.lte.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
/*
TODO : 사용자에 Category 리스트 추가 고려 -> 양방향 메서드,
사용자와의 관계 수정 (뭐 하나는 남겨야한다든가 그런거)
 todo 와의 관계 설정
 */
public class Category extends BaseEntity {

    /** 필드 **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE) //@Setter 없어도 안전하게 방지?
    private Long id;

    @Column(nullable = false)
    private String categoryName = "공부";

    /** 연관관계 **/
    // 사용자 일대다 카테고리
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /** 양방향 메서드 **/

    /** 생성자 및 비즈니스 로직 **/

}
