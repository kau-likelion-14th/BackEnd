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
 * 팔로우 관련 비즈니스 로직을 담당하는 서비스 계층입니다.
 * 컨트롤러는 요청을 받아 서비스에 위임하고, 서비스는 DB 조회·저장·검증 등 실제 처리 흐름을 수행합니다.
 *
 * @Service : 이 클래스를 스프링 빈으로 등록하며, 서비스 계층의 컴포넌트임을 나타냅니다.
 * @RequiredArgsConstructor : final 필드에 대한 생성자를 생성하여, 생성자 주입으로 Repository를 주입받습니다.
 *
 * ---------------------------------------------------------------------------------------------
 * [트랜잭션(Transaction)에 대한 설명]
 * ---------------------------------------------------------------------------------------------
 * 트랜잭션은 "여러 DB 작업을 하나의 작업 단위로 묶어, 전부 성공하면 반영(commit)하고,
 * 중간에 실패하면 전부 취소(rollback)하는" 메커니즘입니다.
 *
 * 1) @Transactional (쓰기 트랜잭션)
 *    - 메서드 실행 시작 시 트랜잭션이 시작되고, 메서드가 정상 종료되면 commit 됩니다.
 *    - 메서드 실행 중 unchecked 예외(RuntimeException 등)가 발생하면 자동으로 rollback 됩니다.
 *    - 같은 트랜잭션 안에서는 여러 번 DB에 접근해도 하나의 연결/세션으로 일관된 데이터를 보게 됩니다.
 *    - 팔로우 추가·삭제처럼 "데이터를 변경하는" 메서드에 붙입니다.
 *
 * 2) @Transactional(readOnly = true) (읽기 전용 트랜잭션)
 *    - 데이터를 변경하지 않고 조회만 할 때 사용합니다.
 *    - readOnly = true 이면 DB에 따라 읽기 전용 연결을 쓰거나, 변경 감지(dirty checking)를 하지 않아 성능이 좋아질 수 있습니다.
 *    - 조회만 하는 메서드에 붙이면, 실수로 엔티티를 수정해도 flush 되지 않도록 할 수 있습니다.
 *    - 팔로워/팔로잉 목록 조회, 검색 등 "조회만 하는" 메서드에 붙입니다.
 */
@Service
@RequiredArgsConstructor
public class FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;


    /**
     * 검색어를 "닉네임" 또는 "닉네임#태그" 형식으로 파싱하여 UserNameDto로 반환하는 내부 유틸 메서드입니다.
     * 태그가 포함된 경우 4자리여야 하며, 그렇지 않으면 INVALID_HANDLE_FORMAT 예외를 던집니다.
     */
    private static UserNameDto nameParsing(String name){
        // "#"이 없으면 닉네임만 있는 검색입니다. userName만 채우고 userTag는 null로 둡니다.
        if(!name.contains("#")) {
            return new UserNameDto(name,null);
        }
        // "#" 기준으로 최대 2개로 나눕니다. 예: "홍길동#1234" → ["홍길동", "1234"]
        String[] parts = name.split("#",2);
        // 태그는 8자리여야 합니다. 아니면 잘못된 형식으로 보고 예외를 던집니다.
        if(parts[1].length() != 8){
            throw new GeneralException(ErrorCode.INVALID_HANDLE_FORMAT);
        }
        return new UserNameDto(parts[0],parts[1]);
    }

    /**
     * 팔로우 관계를 추가합니다.
     * 자기 자신을 팔로우하는 것은 허용하지 않으며, 이미 존재하는 관계면 FOLLOW_ALREADY_EXISTS 예외를 던집니다.
     *
     * [트랜잭션] @Transactional
     * - 이 메서드 안의 모든 DB 작업(조회 2번 + exists 확인 + save)이 하나의 트랜잭션으로 실행됩니다.
     * - 메서드가 정상 끝나면 commit 되어 Follow 한 건이 DB에 반영됩니다.
     * - 예외가 나면 rollback 되어 save 이전 상태로 되돌아가며, 반쪽만 반영되는 상황을 막습니다.
     */
    @Transactional
    public FollowUserResponse followUser(Long fromUserId, Long toUserId) {
        // 팔로우를 하는 사람(fromUser)과 대상(toUser)을 DB에서 조회합니다. 없으면 각각 USER_NOT_FOUND, FOLLOW_TARGET_NOT_FOUND 예외를 던집니다.
        User fromUser = userRepository.findById(fromUserId).orElseThrow(()->new GeneralException(ErrorCode.USER_NOT_FOUND));
        User toUser = userRepository.findById(toUserId).orElseThrow(()->new GeneralException(ErrorCode.FOLLOW_TARGET_NOT_FOUND));

        // 자기 자신을 팔로우하는 것은 허용하지 않습니다.
        if(fromUserId.equals(toUserId)){
            throw new GeneralException(ErrorCode.FOLLOW_SELF_NOT_ALLOWED);
        }
        // 이미 팔로우 관계가 있으면 중복 추가를 막습니다.
        if(followRepository.existsByFromUserAndToUser(fromUser,toUser)){
            throw new GeneralException(ErrorCode.FOLLOW_ALREADY_EXISTS);
        }
        
        // Follow 엔티티를 빌더로 생성한 뒤 DB에 저장합니다.
        Follow follow = Follow.builder()
                .toUser(toUser)
                .fromUser(fromUser)
                .build();
        
        followRepository.save(follow);
        // 응답용으로는 팔로우 대상(toUser) 정보만 DTO로 변환하여 반환합니다.
        return FollowUserResponse.from(follow.getToUser());
    }

    /**
     * 팔로우 관계를 해제합니다. fromUser가 toUser를 팔로우한 Follow 레코드 한 건을 DB에서 삭제합니다.
     * 해당 관계가 존재하지 않으면 FOLLOW_NOT_FOUND 예외를 던집니다.
     *
     * [트랜잭션] @Transactional
     * - 조회 2번 + findBy + delete 가 한 트랜잭션 안에서 실행됩니다.
     * - 예외 시 rollback 되므로 "User는 조회됐는데 delete만 실패" 같은 불일치가 나지 않습니다.
     */
    @Transactional
    public void unfollowUser(Long from_userId, Long to_userId) {
        // fromUser, toUser를 DB에서 조회합니다. 없으면 예외를 던집니다.
        User fromUser = userRepository.findById(from_userId).orElseThrow(()->new GeneralException(ErrorCode.USER_NOT_FOUND));
        User toUser = userRepository.findById(to_userId).orElseThrow(()->new GeneralException(ErrorCode.FOLLOW_TARGET_NOT_FOUND));
        // 자기 자신에 대한 언팔로우는 의미가 없으므로 막습니다.
        if(from_userId.equals(to_userId)){
            throw new GeneralException(ErrorCode.FOLLOW_SELF_NOT_ALLOWED);
        }
        
        // fromUser → toUser 팔로우 관계 한 건을 찾습니다. 없으면 FOLLOW_NOT_FOUND 예외를 던집니다.
        Follow follow = followRepository.findByFromUserAndToUser(fromUser, toUser)
                .orElseThrow(() -> new GeneralException(ErrorCode.FOLLOW_NOT_FOUND));

        followRepository.delete(follow);
    }

    /**
     * 팔로워 목록(나를 팔로우하는 사람들)을 조회합니다.
     *
     * [트랜잭션] @Transactional(readOnly = true)
     * - 조회만 하므로 읽기 전용 트랜잭션을 사용합니다. JPA는 변경 감지(dirty checking)를 하지 않아 부담이 줄어듭니다.
     * - 같은 트랜잭션 안에서 user.getFollowers()로 연관 데이터를 읽을 때 일관된 스냅샷을 보게 됩니다.
     */
    @Transactional(readOnly = true)
    public List<FollowUserResponse> getFollowers(Long userId) {
        // 해당 userId의 User를 조회합니다. (나를 팔로우하는 사람 = toUser가 나인 Follow들의 fromUser)
        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        // user.getFollowers()는 "나(toUser)를 팔로우한 Follow" 목록입니다. 각 Follow에서 "팔로우한 사람(fromUser)"만 꺼내 DTO로 변환합니다.
        return user.getFollowers().stream()
                .map(f -> FollowUserResponse.from(f.getFromUser()))
                .toList();
    }

    /**
     * 팔로잉 목록(내가 팔로우하는 사람들)을 조회합니다.
     *
     * [트랜잭션] @Transactional(readOnly = true) : 조회 전용이므로 읽기 전용 트랜잭션으로 실행합니다.
     */
    @Transactional(readOnly = true)
    public List<FollowUserResponse> getFollowings(Long userId) {
        // 해당 userId의 User를 조회합니다. (내가 팔로우하는 사람 = fromUser가 나인 Follow들의 toUser)
        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        // user.getFollowings()는 "내가(fromUser) 팔로우한 Follow" 목록입니다. 각 Follow에서 "팔로우 대상(toUser)"만 꺼내 DTO로 변환합니다.
        return user.getFollowings().stream()
                .map(f -> FollowUserResponse.from(f.getToUser()))
                .toList();
    }

    /**
     * 팔로우 가능한 유저를 검색합니다. 닉네임만 입력하거나 "닉네임#태그"로 정확히 검색할 수 있습니다.
     * 태그가 있으면 해당 유저 1명만 조회하고, 없으면 닉네임에 포함된 유저 중 자기 자신과 이미 팔로우한 유저는 제외합니다.
     *
     * [트랜잭션] @Transactional(readOnly = true) : 여러 번 조회(user, target, exists 등)가 한 트랜잭션 안에서 일관되게 수행됩니다.
     */
    @Transactional(readOnly = true)
    public Page<FollowUserResponse> serchCanFollowers(Long userId, String targetName, Pageable pageable){
        User user = userRepository.findById(userId).orElseThrow(()->new GeneralException(ErrorCode.USER_NOT_FOUND));
        // 검색어를 "닉네임" / "닉네임#태그"로 파싱합니다.
        UserNameDto nameDto = nameParsing(targetName);
        
        Page<User> users;
        
        // 태그가 있으면 해당 태그로 유저 1명만 정확히 조회합니다.
        if(nameDto.userTag() != null && !nameDto.userTag().isBlank()){
            User target = userRepository.findByUserTag(nameDto.userTag()).orElse(null);
            if(target == null){
                return Page.empty(pageable);
            }
            // 1명만 있으므로 PageImpl로 감싸서 반환 형식을 맞춥니다.
            users = new PageImpl<>(List.of(target), pageable, 1);
        } else {
            // 태그가 없으면 닉네임에 targetName이 포함된 유저들을 페이징 조회합니다. (대소문자 무시)
            users = userRepository.findByUsernameContainingIgnoreCase(nameDto.userName(), pageable);
        }
        
        // 조회된 유저 중에서: 자기 자신은 제외하고, 이미 팔로우 중인 사람도 제외합니다. (팔로우 가능한 유저만 남깁니다)
        List<User> canFollowUsers = users.getContent().stream()
                .filter(target -> !target.getId().equals(userId))
                .filter(target -> !followRepository.existsByFromUserAndToUser(user, target))
                .toList();
        
        // List를 Page 형태로 감싼 뒤, 각 User를 FollowUserResponse DTO로 변환하여 반환합니다.
        return new PageImpl<>(canFollowUsers, pageable, canFollowUsers.size())
                .map(FollowUserResponse::from);
    }

    /**
     * 팔로우 가능한 유저 목록을 페이징하여 조회합니다.
     * UserRepository.findCanFollowUsers에서 자기 자신 및 이미 팔로우한 유저를 제외한 목록을 반환합니다.
     *
     * [트랜잭션] @Transactional(readOnly = true) : 페이징 조회를 읽기 전용 트랜잭션으로 실행합니다.
     */
    @Transactional(readOnly = true)
    public Page<FollowUserResponse> getCanFollowers(Long userId, Pageable pageable){
        // UserRepository에서 "자기 자신 + 이미 팔로우한 유저"를 제외한 팔로우 가능 유저 목록을 페이징 조회합니다.
        Page<User> canFollowUsers = userRepository.findCanFollowUsers(userId, pageable);
        // 각 User를 FollowUserResponse DTO로 변환한 Page를 반환합니다.
        return canFollowUsers.map(FollowUserResponse::from);
    }
}
