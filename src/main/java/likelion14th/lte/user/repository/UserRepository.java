package likelion14th.lte.user.repository;

import likelion14th.lte.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}