package likelion14th.lte.user.service;

import likelion14th.lte.global.api.ErrorCode;
import likelion14th.lte.global.exception.GeneralException;
import likelion14th.lte.user.dto.response.UserProfileResponse;
import likelion14th.lte.user.domain.User;
import likelion14th.lte.user.repository.UserRepository;
import likelion14th.lte.utils.S3.S3Dto;
import likelion14th.lte.utils.S3.S3Utils;
import likelion14th.lte.utils.Image.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import likelion14th.lte.utils.exception.UtilException;


@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;
    private final S3Utils s3Utils;
    private final ImageUtil imageUtil;

    @Transactional
    public UserProfileResponse putProfileImage(Long userId,MultipartFile file) {

        User user=userRepository.findById(userId)
                .orElseThrow(()->new GeneralException(ErrorCode.USER_NOT_FOUND));

        try{
            //이미지 검증
            imageUtil.validateImage(file);
            //이미지 변환
            ImageUtil.ResizedImage resized =
                    imageUtil.resizeProfileToWebpBytes(file, 256);
            //유저 이미지 이미 존재한다면 삭제;
            if(user.getS3ImageKey()!=null){
                s3Utils.deleteFile(user.getS3ImageKey());
            }

            S3Dto result=s3Utils.uploadBytes(resized.bytes(),file.getOriginalFilename(), resized.contentType());
            user.setProfileImage(result.getUrl());
            user.setS3ImageKey(result.getKey());

            return UserProfileResponse.from(user);

        } catch (UtilException e) {
            throw GeneralException.of(mapToErrorCode(e.getReason()));
        }
    }

    @Transactional
    public void deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new GeneralException(ErrorCode.USER_NOT_FOUND));

        if(user.getS3ImageKey()!=null){
            s3Utils.deleteFile(user.getS3ImageKey());
        }

        user.setProfileImage(null);
        user.setS3ImageKey(null);

    }

    private ErrorCode mapToErrorCode(UtilException.Reason reason) {
        return switch (reason) {
            case FILE_EMPTY -> ErrorCode.IMAGE_FILE_EMPTY;
            case FILE_TOO_LARGE -> ErrorCode.IMAGE_TOO_LARGE;
            case TYPE_NOT_ALLOWED -> ErrorCode.IMAGE_TYPE_NOT_ALLOWED;

            case IMAGE_PROCESS_FAILED -> ErrorCode.IMAGE_PROCESS_FAILED;

            case S3_UPLOAD_FAILED -> ErrorCode.S3_UPLOAD_FAILED;
            case S3_DELETE_FAILED -> ErrorCode.S3_DELETE_FAILED;
        };
        }

}