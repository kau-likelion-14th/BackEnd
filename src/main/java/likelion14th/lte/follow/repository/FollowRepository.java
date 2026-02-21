package likelion14th.lte.follow.repository;

import likelion14th.lte.follow.domain.Follow;
import likelion14th.lte.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *  Follow 엔티티에 대한 DB 접근 레이어.
 * JpaRepository<Follow, Long>: Follow 엔티티, PK 타입 Long에 대한 CRUD + 페이징 등 제공.
 */
public interface FollowRepository extends JpaRepository<Follow,Long> {
    /**  Spring Data JPA 메서드 네이밍: existsBy + 필드명 → "fromUser와 toUser로 존재 여부" 쿼리 자동 생성 */
    Boolean existsByFromUserAndToUser(User fromUser, User toUser);
    /**  findBy + 필드명: 해당 조건의 Follow 한 건 조회. 없을 수 있으므로 Optional 반환 */
    Optional<Follow> findByFromUserAndToUser(User fromUser, User toUser);
}
