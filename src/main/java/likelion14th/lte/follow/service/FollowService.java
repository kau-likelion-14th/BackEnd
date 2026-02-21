package likelion14th.lte.follow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import likelion14th.lte.follow.dto.FollowUserResponse;
import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.exception.GeneralException;

import likelion14th.lte.user.repository.UserRepository;
import likelion14th.lte.follow.repository.FollowRepository;

import likelion14th.lte.follow.domain.Follow;
import likelion14th.lte.user.domain.User;
import likelion14th.lte.follow.dto.UserNameDto;

import java.util.List;

/**
 *  팔로우 비즈니스 로직을 담당하는 서비스 계층.
 * 팔로우/언팔로우, 팔로워·팔로잉 목록 조회, 팔로우 가능 유저 검색 등을 처리합니다.
 *
 * @Service: Spring이 이 클래스를 빈으로 등록하고, 비즈니스 로직 계층으로 인식.
 * @RequiredArgsConstructor: final 필드(UserRepository, FollowRepository)에 대한 생성자 자동 생성 → 생성자 주입으로 의존성 주입.
 */
@Service
@RequiredArgsConstructor
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;


    /**
     *  검색어 파싱: "닉네임" 또는 "닉네임#태그" 형식을 UserNameDto로 분리.
     * 태그가 있으면 4자리여야 하며, 아니면 INVALID_HANDLE_FORMAT 예외.
     */
    private static UserNameDto nameParsing(String name){
        if(!name.contains("#")) {
            return new UserNameDto(name,null);
        }
        String[] parts = name.split("#",2);
        if(parts[1].length() != 4){
            throw new GeneralException(ErrorCode.INVALID_HANDLE_FORMAT);
        }
        return new UserNameDto(parts[0],parts[1]);
    }

    /**
     *  팔로우 추가.
     * @Transactional: 이 메서드 전체를 하나의 DB 트랜잭션으로 실행. 예외 시 롤백.
     * - 자기 자신 팔로우 불가, 이미 존재하는 관계면 FOLLOW_ALREADY_EXISTS 처리.
     */
    @Transactional
    public FollowUserResponse followUser(Long fromUserId, Long toUserId) {
        User fromUser = userRepository.findById(fromUserId).orElseThrow(()->new GeneralException(ErrorCode.USER_NOT_FOUND));
        User toUser = userRepository.findById(toUserId).orElseThrow(()->new GeneralException(ErrorCode.FOLLOW_TARGET_NOT_FOUND));

        if(fromUserId.equals(toUserId)){
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
        return FollowUserResponse.from(follow.getToUser());
    }

    /**
     *  언팔로우: fromUser → toUser 팔로우 관계 삭제.
     * 관계가 없으면 FOLLOW_NOT_FOUND 예외.
     */
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

    /**
     *  팔로워 목록: 나(toUser)를 팔로우하는 사람들 조회.
     * @Transactional(readOnly = true): 읽기 전용 트랜잭션으로 변경 감지 비활성화, 조회 성능에 유리.
     */
    @Transactional(readOnly = true)
    public List<FollowUserResponse> getFollowers(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        return user.getFollowers().stream()
                .map(f -> FollowUserResponse.from(f.getFromUser()))
                .toList();
    }

    /**
     *  팔로잉 목록: 내가(fromUser) 팔로우하는 사람들 조회.
     */
    @Transactional(readOnly = true)
    public List<FollowUserResponse> getFollowings(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        return user.getFollowings().stream()
                .map(f -> FollowUserResponse.from(f.getToUser()))
                .toList();
    }

    /**
     *  팔로우 가능한 유저 검색: 닉네임 또는 "닉네임#태그"로 검색.
     * userTag가 있으면 해당 유저 1명만, 없으면 username 포함 검색 후 자기 자신·이미 팔로우한 유저 제외.
     */
    @Transactional(readOnly = true)
    public Page<FollowUserResponse> serchCanFollowers(Long userId, String targetName, Pageable pageable){
        User user = userRepository.findById(userId).orElseThrow(()->new GeneralException(ErrorCode.USER_NOT_FOUND));
        UserNameDto nameDto = nameParsing(targetName);
        
        Page<User> users;
        
        if(nameDto.userTag() != null && !nameDto.userTag().isBlank()){
            User target = userRepository.findByUserTag(nameDto.userTag()).orElse(null);
            if(target == null){
                return Page.empty(pageable);
            }
            users = new PageImpl<>(List.of(target), pageable, 1);
        } else {
            users = userRepository.findByUsernameContainingIgnoreCase(nameDto.userName(), pageable);
        }
        
        List<User> canFollowUsers = users.getContent().stream()
                .filter(target -> !target.getId().equals(userId))
                .filter(target -> !followRepository.existsByFromUserAndToUser(user, target))
                .toList();
        
        return new PageImpl<>(canFollowUsers, pageable, canFollowUsers.size())
                .map(FollowUserResponse::from);
    }

    /**
     *  팔로우 가능한 유저 목록 페이징 조회.
     * UserRepository.findCanFollowUsers에서 "자기 자신·이미 팔로우한 유저 제외" 조건이 적용된 것으로 추정.
     */
    @Transactional(readOnly = true)
    public Page<FollowUserResponse> getCanFollowers(Long userId, Pageable pageable){
        Page<User> canFollowUsers = userRepository.findCanFollowUsers(userId, pageable);
        return canFollowUsers.map(FollowUserResponse::from);
    }
}
