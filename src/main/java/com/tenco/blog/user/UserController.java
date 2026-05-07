package com.tenco.blog.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller // IoC
@RequiredArgsConstructor // DI 처리
public class UserController {

    private final UserService userService;



    // 프로필 수정 기능 요청
    @PostMapping("/user/update")
    public String updateProc(UserRequest.UpdateDTO updateDTO, HttpSession session) {

        // 인증검사 - LoginInterceptor 에서 처리
        // 유효성 검사
        updateDTO.validate();
        UserResponse.SessionDTO sessionUser = (UserResponse.SessionDTO) session.getAttribute("sessionUser");
        userService.회원정보수정(sessionUser.getId(), updateDTO,session);
        return "redirect:/";

    }


    // 프로필 화면 요청
    @GetMapping("/user/update-form")
    public String updateFormPage(HttpSession session, Model model) {
        // 인증검사 -> 로그인 한 상태 임
        UserResponse.SessionDTO sessionUser = (UserResponse.SessionDTO) session.getAttribute("sessionUser");

        UserResponse.SessionDTO sessionDTO = userService.회원정보수정화면(sessionUser.getId());
        model.addAttribute("user", sessionDTO);
        return "user/update-form";
    }


    // 로그인 화면 요청
    // 주소 설계 : http://localhost:8080/login-form
    @GetMapping("/login-form")
    public String loginFormPage() {
        // 인증검사 x , 유효성 검사 x
        return "user/login-form";
    }

    // 로그인 기능 요청
    @PostMapping("/login")
    public String loginProc(UserRequest.LoginDTO reqloginDTO, HttpSession session) {

        // 1. 인증검사 x 유효성 검사 o
        reqloginDTO.validate();

        UserResponse.SessionDTO sessionDTO = userService.로그인(reqloginDTO);
        session.setAttribute("sessionUser", sessionDTO);

        return "redirect:/";
    }


    // 로그아웃 기능 요청
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 세션 메모리에 내 정보를 없애 버림
        // 로그아웃
        session.invalidate();
        return "redirect:/";
    }

    // 회원가입 화면 요청
    // 주소 설계 : http://localhost:8080/join-form
    @GetMapping("/join-form")
    public String joinFormPage() {
        return "user/join-form";
    }

    // 회원가입 기능 요청
    // 주소 설계 : http://localhost:8080/join
    @PostMapping("/join")
    // 메세지 컨버터라는 녀석이 구문을 분석해서 자동으로 파싱처리
    // 파싱 전략 1 - key=value 구조 (@RequestParam 사용)
    // 파싱 전력 2 - Object DTO 설계
    public String joinProc(UserRequest.JoinDTO joinDTO) {


        // 1. 유효성 검사하기
        joinDTO.validate(); // 유효성 검사 --> 오류가있다면 예외처리로 넘어감
        userService.회원가입(joinDTO);




        // 로그인 화면으로 리다이렉트
        return "redirect:/login-form";
    }


}
