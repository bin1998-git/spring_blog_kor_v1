package com.tenco.blog.board;

import lombok.Builder;
import lombok.Data;

// 요청 데이터를 담는 DTO 클래스
// 컨트롤러 비즈니스, 데이터 계층 사이에서 데이터 전송 역할
public class BoardRequest {

    @Data
    @Builder
    public static class SaveDTO {
        private String username;
        private String title;
        private String content;

        // 편의 기능 설계 가능
        // DTO 에서 Entity로 변환해주는 편의 메서드
        public Board toEntity() {
            return Board.builder()
                    .username(username)
                    .title(title)
                    .content(content)
                    .build();
        }
    }


    // 내부 정적 클래스 게시글 수정 DTO설계
    @Data
    @Builder
    public static class UpdateDTO {
        private String username;
        private String title;
        private String content;



        // 게시글 수정시 유효성 검사
        public void validate() {
            if (content == null || content.length() < 3) {
                throw new IllegalArgumentException("내용은 3글자 이상 작성하시오");
            } else if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("제목은 필수 입니다");
            } else if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("이름은 필수입니다");
            }
        }


    }


}
