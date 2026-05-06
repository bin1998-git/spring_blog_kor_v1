package com.tenco.blog.board;


import com.tenco.blog._core.errors.*;
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

    private final BoardService boardService;

    /**
     * 게시글 작성 화면 요청
     *
     * @return 페이지 반환
     * 주소설계 : http://localhost:8080/board/save-form
     */
    @GetMapping("/board/save-form")
    public String saveForm(HttpSession httpSession) {
        // 1. 인증 검사는 LoginInterceptor 에서 먼저 처리함
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
        User sessionUser = (User) session.getAttribute("sessionUser");
        saveDTO.validate();
        boardService.save(saveDTO, sessionUser);
        return "redirect:/";
        // 3.


    }


    /**
     * 게시글 목록 화면 요청
     * 주소설계 : http://localhost:8080/
     */
    @GetMapping({"/", "index"})
    public String list(Model model) {

        List<Board> boardList = boardService.findAll();
        model.addAttribute("boardList", boardList);
        return "board/list";
    }


    // 게시글 상세보기 화면 요청
    // http://localhost:8080/board/1
    @GetMapping("/board/{id}")
    public String detailPage(@PathVariable(name = "id") Integer id, Model model) {

        Board board = boardService.findById(id);


        model.addAttribute("board", board);


        return "board/detail";
    }


    // 삭제 기능 요청
    // 1. 로그인여부를 확인
    // 2. 삭제할 게시글이 보인이 작성한 글인지 확인 (권한 확인, 인가처리)
    // 3. 인가 처리후 삭제 진행
    @PostMapping("/board/{id}/delete")
    public String deleteProc(@PathVariable(name = "id") Integer id, HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        boardService.deleteById(id, sessionUser);

        return "redirect:/";
    }

    // http://localhost:8080/board/1/update-form
    // 게시글 수정 화면 요청
    @GetMapping("/board/{id}/update-form")
    public String updateFormPage(@PathVariable(name = "id") Integer id, Model model, HttpSession session) {

        User sessionUser = (User) session.getAttribute("sessionUser");
        // findById <----- 상세보기 화면 요청이라서 누군나 요청이 가능
        // 즉 인가처리가 안되고 있음
        Board boardEntity = boardService.findByIdAndCheckOwner(id, sessionUser);
        model.addAttribute("board", boardEntity);
        return "board/update-form";

    }

    // /board/{id}/update
    @PostMapping("/board/{id}/update")
    public String UpdateProc(@PathVariable Integer id, BoardRequest.UpdateDTO updateDTO, HttpSession session) {



        User sessionUser = (User) session.getAttribute("sessionUser");
        updateDTO.validate();

        boardService.updateById(id, updateDTO, sessionUser);
        return "redirect:/board/" + id;
    }
}
