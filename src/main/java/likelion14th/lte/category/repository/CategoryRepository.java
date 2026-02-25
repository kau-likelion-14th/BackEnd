package likelion14th.lte.category.repository;

import likelion14th.lte.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByOrderByCategoryNameAsc();

    Optional<Category> findByCategoryName(String categoryName);
}
