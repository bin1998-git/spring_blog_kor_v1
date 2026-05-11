package com.tenco.blog._core.config;


import com.tenco.blog._core.interceptor.LoginInterceptor;
import com.tenco.blog._core.interceptor.SessionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 자바 코드로 스프링 부트 설정파일을 다룰 수 있다.
// @Component
@Configuration // IoC 대상 - 하나 이상의 IoC 처리를 하고 싶을 떄 사용 한다.
@RequiredArgsConstructor // DI처리
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired // DI 처리
    private  LoginInterceptor loginInterceptor;
    @Autowired // DI 처리
    private SessionInterceptor sessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 화면에 SessionUser 정보를 내려줄 떄 사용 됨
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/**"); //모든 URL 요청에서 동작 함


        // 인증처리 인터셉터 동작
        System.out.println("인터셉터 동작 함 ");

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/board/**", "/user/**")
                .excludePathPatterns(
                        // 로그인 관련(인증이 필요 없는 페이지)
                        "/login-form", // 로그인 화면 요청시
                        "/join-form", // 회원가입 화면 요청시
                        "/logout", // 로그아웃

                        // 게시글 조회 관련 (인증 없이도 볼 수 있는 페이지)
                        "/board/list", // 게시글 목록 리스트 화면 요청 시
                        "/"          , // 메인 페이지
                        "/index"          , // 메인 페이지
                        "/board/{id:\\d+}", // 게시글 상세보기( 숫자 Id만 허용)

                        // 정적 리소스 (CSS, JS, 이미지 등)
                        "/css/**",   // CSS 파일 제외
                        "/js/**",    // JS 파일 제외
                        "/images/**", // Imgages 파일 제외
                        "/favicon.ico", // 파미콘 제외(탭창위 로고)

                        // H2 데이터베이스 콘솔(개발 환경용)
                        "/h2-console/**" // H2 콘솔 접근
                );


    }



}
