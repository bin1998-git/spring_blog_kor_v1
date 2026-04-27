package com.tenco.blog.board;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository // IoC (있어야지 메모리에 올라감)
@RequiredArgsConstructor // final을 사용하더라도 di처리됨
public class BoardPersistRepository {

    // JPA 핵심 인터페이스
    // 영속성 컨텍스트를 관리하고 엔티티의 생명주기를 제어
    // @Autowired // DI
    private final EntityManager em; // 파이널을 사용하게 되면 성능 개선이 조금 됨.( 파이널을 사용시 Autowired 사용 불가)


    // 의존주입 (외부에서 생성되어 있는 객체의 주소값을 주입받다)
//    public BoardPersistRepository(EntityManager em) {
//        this.em = em;
//    }


    // 게시글 저장
    @Transactional
    public Board save(Board board) {
        // 매개 변수로 받은 board는 비영속상태
        // 아직 영속성 컨텍스트에 관리 되고 있지 않는 상태
        // 데이터베이스와 연관 없는 순수 java 객체 일뿐
        em.persist(board); // insert 처리 완료
        // 2. 이 board 객체를 영속성 컨텍스트에 넣어 둠 (sql 저장소에 등록)
        // 영속성 컨텍스트에 들어가더라도 아직 db에 실제 insert가 된건 아님

        //3. 트랜잭션 커밋시점에 실제 db에 접근해서 insert 구문이 실행

        // 4. board 객체의 id변수값을  1차 캐쉬에 map 구조로 보관 되어 짐.

        // 1차 캐쉬에 들어간 이제 영속상태로 변경된 오브젝트를 리턴
        return board;

    }

    // JPQL을 사용한 게시글 목록 조회
    public List<Board> findAll() {

        // JPQL : 엔티티 객체를 대상으로 하는 객체지향 쿼리
        // Board는 엔티티 클래스 명, b는 별칭
        // 주의! 테이블명이 아니라 엔티티 사용
        String jpql = """
                SELECT b FROM Board b ORDER BY b.createdAt DESC
                """;

        List<Board> boardList = em.createQuery(jpql, Board.class).getResultList();

        return boardList;
    }


}
