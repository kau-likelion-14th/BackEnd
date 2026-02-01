package likelion14th.lte.follow.repository;

import likelion14th.lte.follow.domain.Follow;
import likelion14th.lte.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow,Long> {
    Boolean existsByFromUserAndToUser(User fromUser, User toUser);
    Optional<Follow> findByFromUserAndToUser(User fromUser, User toUser);
    void deleteByFromUserAndToUser(User fromUser, User toUser);
}
