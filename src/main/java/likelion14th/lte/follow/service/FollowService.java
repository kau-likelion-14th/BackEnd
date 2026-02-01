package likelion14th.lte.follow.service;

import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.exception.GeneralException;
import likelion14th.lte.user.domain.User;
import likelion14th.lte.user.repository.UserRepository;
import likelion14th.lte.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import likelion14th.lte.follow.domain.Follow;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional
    public void followUser(Long from_userId, Long to_userId) {
        User fromUser = userRepository.findById(from_userId).orElseThrow(()->new GeneralException(ErrorCode.USER_NOT_FOUND));
        User toUser = userRepository.findById(to_userId).orElseThrow(()->new GeneralException(ErrorCode.FOLLOW_TARGET_NOT_FOUND));

        if(from_userId.equals(to_userId)){
            throw new GeneralException(ErrorCode.FOLLOW_SELF_NOT_ALLOWED);
        }
        if(followRepository.existsByFromUserAndToUser(fromUser,toUser)){
            throw new GeneralException(ErrorCode.FOLLOW_ALREADY_EXISTS);
        }
        
        Follow follow = Follow.builder()
                .toUser(toUser)
                .fromUser(fromUser)
                .build();
        
        followRepository.save(follow);
    }

    @Transactional
    public void unfollowUser(Long from_userId, Long to_userId) {
        User fromUser = userRepository.findById(from_userId).orElseThrow(()->new GeneralException(ErrorCode.USER_NOT_FOUND));
        User toUser = userRepository.findById(to_userId).orElseThrow(()->new GeneralException(ErrorCode.FOLLOW_TARGET_NOT_FOUND));
        if(from_userId.equals(to_userId)){
            throw new GeneralException(ErrorCode.FOLLOW_SELF_NOT_ALLOWED);
        }
        
        Follow follow = followRepository.findByFromUserAndToUser(fromUser, toUser)
                .orElseThrow(() -> new GeneralException(ErrorCode.FOLLOW_NOT_FOUND));

        followRepository.delete(follow);
    }


}
