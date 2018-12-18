package codesquad.web;


import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("")
    public String form(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @PutMapping("/{id}")
    public String create(Question updateQuestion, @PathVariable long id, @LoginUser User loginUser) {
        Question question = qnaService.findById(id);
        if (question.isOwner(loginUser)) {
            question.modify(updateQuestion, loginUser);
            qnaService.add(question);
        }
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model, @LoginUser User loginUser) {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        Question question = qnaService.findById(id);
        if (question.isOwner(loginUser)) {
            model.addAttribute("question", question);
            return "/qna/updateForm";
        }
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String deleteForm(@LoginUser User loginUser, @PathVariable long id) {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }
}
