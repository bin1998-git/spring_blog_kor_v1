package com.tenco.blog.user;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository // IOC
@RequiredArgsConstructor
public class UserPersistRepository {

    // DI - 스프링 프레임 워크가 주소값 자동 주입
    private final EntityManager em;

    public User findById(Integer id) {
       User user = em.find(User.class, id);
       if (user == null) {
           throw new RuntimeException("사용자를 찾을 수 없습니다");

       }
       return user;
    }


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


    @Transactional
    public User updateById(Integer id, UserRequest.UpdateDTO updateDTO) {

        User userEntity = findById(id); // 영속성 컨텍스트에 관리되는 엔티티

        userEntity.setPassword(updateDTO.getPassword()); // 객체의 상태값 변경

        return userEntity;
    }
}
