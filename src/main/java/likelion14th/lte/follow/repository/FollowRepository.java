package likelion14th.lte.follow.repository;

import likelion14th.lte.follow.domain.Follow;
import likelion14th.lte.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Follow 엔티티에 대한 DB 접근을 담당하는 레포지토리 인터페이스입니다.
 * JpaRepository<Follow, Long>를 상속하면 save, findById, delete 등 기본 CRUD 메서드가 자동으로 제공됩니다.
 * 제네릭의 첫 번째 인자는 엔티티 타입, 두 번째 인자는 기본키(id)의 타입입니다.

 * 아래 메서드는 Spring Data JPA의 메서드 네이밍 규칙을 따르면, 구현 없이 쿼리가 자동 생성됩니다.
 */
public interface FollowRepository extends JpaRepository<Follow,Long> {
    /** 주어진 fromUser와 toUser 조합에 해당하는 Follow 관계가 존재하는지 여부를 반환합니다. (예: 중복 팔로우 방지 시 사용) */
    Boolean existsByFromUserAndToUser(User fromUser, User toUser);
    /** 주어진 fromUser와 toUser에 해당하는 Follow 한 건을 조회합니다. 없을 수 있으므로 Optional로 반환합니다. (예: 언팔로우 시 삭제할 레코드 조회) */
    Optional<Follow> findByFromUserAndToUser(User fromUser, User toUser);
}
