package likelion14th.lte.follow.dto;

/**
 *  검색어 파싱 결과를 담는 불변 DTO.
 * record: Java 16+ 문법. 생성자, getter, equals/hashCode가 자동 생성되며, 필드가 불변(immutable)입니다.
 * "닉네임" 또는 "닉네임#태그" 형식으로 들어온 검색어를 userName / userTag로 분리해 전달할 때 사용합니다.
 */
public record UserNameDto(String userName, String userTag) {}
