package likelion14th.lte.category.service;

import jakarta.transaction.Transactional;
import likelion14th.lte.category.domain.Category;
import likelion14th.lte.category.dto.response.CategoryResponse;
import likelion14th.lte.category.repository.CategoryRepository;
import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.exception.GeneralException;
import likelion14th.lte.user.domain.User;
import likelion14th.lte.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /** 카테고리 목록 조회 **/
    @Transactional
    public List<CategoryResponse> getAllCategories(){
        // 카테고리 목록 조회
        List<Category> categories = categoryRepository.findAllByOrderByCategoryNameAsc();

        // Dto 로 변환 후 반환
        return categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

}
