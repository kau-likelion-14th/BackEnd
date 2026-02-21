package likelion14th.lte.follow.dto;

/**
 * 검색어 파싱 결과를 담는 DTO입니다.
 * "닉네임"만 입력되면 userName만 채우고, "닉네임#태그" 형식이면 userName과 userTag를 분리하여 담습니다.
 *
 * record는 Java 16에서 도입된 문법으로, 필드만 선언하면 생성자·getter·equals·hashCode가 자동 생성됩니다.
 * 인스턴스 생성 후 필드 값을 변경할 수 없는 불변(immutable) 객체입니다.
 */
public record UserNameDto(String userName, String userTag) {}
