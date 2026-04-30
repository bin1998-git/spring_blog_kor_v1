package com.tenco.blog.board;


import com.tenco.blog.user.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.IllegalFormatCodePointException;
import java.util.List;

@Slf4j
@Controller // IoC
@RequiredArgsConstructor // DI

public class BoardController {
    // DI
    private final BoardPersistRepository boardPersistRepository;

    /**
     * 게시글 작성 화면 요청
     *
     * @return 페이지 반환
     * 주소설계 : http://localhost:8080/board/save-form
     */
    @GetMapping("/board/save-form")
    public String saveForm(HttpSession httpSession) {

        // 로그인 여부 체크 - 즉 로그인 한 사용자만 이 페이지 안에 들어올 수 있음
        // 1. 인증 검사
        User sessionUser = (User) httpSession.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form";
        }


        return "board/save-form";
    }

    /**
     * 게시글 작성 기능 요청
     *
     * @return 페이지 반환
     * 주소설계 : http://localhost:8080/board/save-form
     */
    @PostMapping("/board/save")
    // 사용자 요청 -> http 요청 메세지(post)
    public String saveProc(BoardRequest.SaveDTO saveDTO, HttpSession session) {

        log.info("== 게시글 저장 요청===");
        // 이 요청 시 사용자가 로그인을 했다면 로그인 정보를 세션 메모리에서 가져오면 된다.
        // 1. 세션에서 로그인한 사용자 정보 가져오기
        User sessionUser = (User) session.getAttribute("sessionUser");

        // 2. 로그인 여부 확인
        if (sessionUser == null) {
            return "redirect:/login-form";
        }
        try {
            // 3. 로그인 된 사용자
            // 3-1 유효성 검사
            saveDTO.validate();
            boardPersistRepository.save(saveDTO.toEntity(sessionUser));
            return "redirect:/";
        } catch (Exception e) {
            System.out.println("에러 발생 : " + e.getMessage());
            return "board/save-form";
        }

    }


    /**
     * 게시글 목록 화면 요청
     * 주소설계 : http://localhost:8080/
     */
    @GetMapping({"/", "index"})
    public String list(Model model) {

        List<Board> boardList = boardPersistRepository.findAll();
        model.addAttribute("boardList", boardList);
        return "board/list";
    }


    // 게시글 상세보기 화면 요청
    // http://localhost:8080/board/1
    @GetMapping("/board/{id}")
    public String detailPage(@PathVariable(name = "id") Integer id, Model model) {

        Board board = boardPersistRepository.findById(id);
        // board는 연관관계가 User 엔티티와 ManytoOne 관계로 설정이 되어 있다
        //  직접 쿼리구문을 작성하지 않을 떄 즉, 엔티티 매니저의 메서드로 객체를 조회시
        // 자동으로 join 구문을 호출해 준다.
        // 단 Fatch 전략에 따라 이걸 EAGER, LAZY 전략에 따라 한번에 다 조인해서 가져오거나
        // 필요할 떄 한번 더 요청하는것이 LAZY 전략
        // 코드상에서 User 에 정보를 요구 (현재 LAZY 전략)
        //System.out.println(board.getUser().getUsername());


        model.addAttribute("board", board);


        return "board/detail";
    }


    // 삭제 기능 요청
    // 1. 로그인여부를 확인
    // 2. 삭제할 게시글이 보인이 작성한 글인지 확인 (권한 확인, 인가처리)
    // 3. 인가 처리후 삭제 진행
    @PostMapping("/board/{id}/delete")
    public String deleteProc(@PathVariable(name = "id") Integer id, HttpSession session) {
        log.info("=== 게시글 삭제 요청 ===");
        // 인증 검사
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form";
        }

        try {
            // 삭제 할 게시글 조회 (권한 체크, 인가 처리)
            Board board = boardPersistRepository.findById(id);
            if (board.getUser().getId() == sessionUser.getId()) {
                boardPersistRepository.deleteById(id);
            }

        } catch (Exception e) {
            return "redirect:/";
        }


        return "redirect:/";
    }

    // http://localhost:8080/board/1/update-form
    // 게시글 수정 화면 요청
    @GetMapping("/board/{id}/update-form")
    public String updateFormPage(@PathVariable(name = "id") Integer id, Model model, HttpSession session) {

        // 인증 처리
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form";
        }

        // 인가 처리
        Board board = boardPersistRepository.findById(id);
        if (sessionUser.getId() != board.getUser().getId()) {
            // 본인이 작성한 글이 맞음
            throw new RuntimeException("수정 권한이 없습니다");
        }


        model.addAttribute("board", board);
        return "board/update-form";
    }

    // /board/{id}/update
    @PostMapping("/board/{id}/update")
    public String UpdateProc(@PathVariable Integer id, BoardRequest.UpdateDTO updateDTO, HttpSession session) {


        // 인증 검사
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login-form";
        }

        try {
            // 유효성 검사
            updateDTO.validate();
            // 인가 검사
            Board board = boardPersistRepository.findById(id);
            if (board.getUser().getId() != sessionUser.getId()) {
                throw new RuntimeException("수정할 권한이 없습니다");
            }
            boardPersistRepository.updateById(id, updateDTO);

        } catch (Exception e) {
            return "redirect:/board/" + id + "update-form";
        }

        return "redirect:/board/" + id;
    }
}
