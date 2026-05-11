package com.tenco.blog.reply;

import com.tenco.blog.board.Board;
import com.tenco.blog.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data // get,set, toString ..
// @Entity - JPA가 이 클래스를 데이터베이스 테이블과 매핑하는 객체로 인식하게 설정
// 즉, 이 어노테이션이 있어야 JPA가 관리 함
@Entity
@Table(name = "reply_tb")
@NoArgsConstructor // 기본 생성자 (필수)
@AllArgsConstructor // 전체 멤버 변수를 넣을 수 있는 생성자.
@Builder // 빌더 패턴
public class Reply {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 500, nullable = false)
    private String comment;

    @CreationTimestamp // pc -> db 자동주입
    private Timestamp created_At;

    // 1 : N , N : 1 , M : N <- 중 선택
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    // 하나 게시글에 여러개의 댓글이 작성가능
    private Board board;

    // 1 : N , N : 1 , M : N <- 중 선택
    // Reply -> User 연관관계 설정 (FK -> 자바에서 표현하는 개념)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;






}
