package com.tenco.blog.user;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository // IOC
@RequiredArgsConstructor
public class UserRepository {

    // DI - 스프링 프레임 워크가 주소값 자동 주입
    private final EntityManager em;


    // 회원 가입 요청시 -->  insert
    @Transactional
    public User save(User user) {
        // 매개 변수로 들어온 User Object는 비영속
        em.persist(user);
        // 여기서의 유저는 영속된 상태이다(em.persist가 되어서)
        return user;
    }


    // 사용자 이름 중복 확인(name에 unique걸려있음)
    public User findByUsername(String username) {
        String jpql = " SELECT u FROM User u WHERE u.username = :username";

        //  Query query = em.createQuery(jpql,User.class);
        //            query.setParameter("username", username);
        //            User user = (User) query.getSingleResult();

        try {
            return em.createQuery(jpql, User.class).setParameter("username", username).getSingleResult();
        } catch (Exception e) {
            return null;
        }


    }


    // 로그인 요청시 --> select
    public User findByUsernameAndPassword(String username, String password) {
        String jpqlStr = """
                    SELECT u FROM User u WHERE u.username = :username  AND u.password = :password
                """;


        try {
            return em.createQuery(jpqlStr, User.class).setParameter("username", username).setParameter("password", password).getSingleResult();

        } catch (Exception e) {
            return null;
        }


    }


}
