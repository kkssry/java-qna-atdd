package codesquad.web;

import codesquad.domain.DeleteHistory;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.DeleteHistoryService;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
    private static final Logger log = getLogger(ApiQuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @Autowired
    private DeleteHistoryService deleteHistoryService;

    @PostMapping("")                                                                     //RequestBody : json객체를 자바객체로 맵핑해라.(set메서드를 통해서)
    public ResponseEntity<Question> create(@LoginUser User loginUser, @Valid @RequestBody Question question) {
        Question savedQuestion = qnaService.create(loginUser, question);
        HttpHeaders headers = new HttpHeaders();                                        //요청한후의 응답
        headers.setLocation(URI.create("/api/questions/" + savedQuestion.getId()));     //요청한후의 응답
        return new ResponseEntity<Question>(headers, HttpStatus.CREATED);               //요청한후의 응답
    }

    @GetMapping("/{id}")
    public Question show(@PathVariable long id) {
        return qnaService.findByQuestionId(id);
    }

    // request Body : json -> 자바객체        // request : json으로 보낸다
    // response Body : 자바객체 -> json

    @PutMapping("/{id}")
    public Question update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Question updateQuestion) {
        return qnaService.update(loginUser, id, updateQuestion);
    }

    @DeleteMapping("/{id}")
    public Question delete(@LoginUser User loginUser, @PathVariable long questionId) {
        Question question = qnaService.deleteQuestion(loginUser,questionId);
        // 로그기록
        deleteHistoryService.saveAll(question.createDeleteHistories(questionId));
        return question;
    }
}
