package likelion14th.lte.user.service;

import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.exception.GeneralException;
import likelion14th.lte.user.dto.request.UserIntroRequest;
import likelion14th.lte.user.dto.response.UserProfileResponse;
import likelion14th.lte.user.domain.User;
import likelion14th.lte.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserIntroService {

    private final UserRepository userRepository;

    @Transactional
    public UserProfileResponse updateUserIntroduce(Long userId, UserIntroRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new GeneralException(ErrorCode.USER_NOT_FOUND));
        user.updateIntroduction(request.getIntroduce());
        return UserProfileResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new GeneralException(ErrorCode.USER_NOT_FOUND));
        return UserProfileResponse.from(user);
    }

}
