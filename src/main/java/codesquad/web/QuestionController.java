package codesquad.web;


import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import codesquad.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form(@LoginUser User loginUser ) {
        return "/qna/form";
    }

    @PostMapping("")
    public String create(Question question) {
        qnaService.add(question);
        return "redirect:/";
    }

//    @GetMapping("/{id}/form")
//    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
//        model.addAttribute("user", userService.findById(loginUser, id));
//        return "/questions/updateForm";
//    }
//
    @GetMapping("/{id}")
    public String show(@PathVariable long id,Model model) {
        model.addAttribute("question",qnaService.findById(id));
        return "/qna/show";
    }
}
