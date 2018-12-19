package codesquad.web;


import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question question) {
        Question savedQuestion = qnaService.create(loginUser, question);
        HttpHeaders headers = new HttpHeaders();                                        //요청한후의 응답
        headers.setLocation(URI.create("/api/questions/" + savedQuestion.getId()));     //요청한후의 응답
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);                   //요청한후의 응답
    }

    @GetMapping("/{id}")
    public Question show(@PathVariable long id) {

        return qnaService.findById(id);
    }

    // request Body : json -> 객체
    // response Body : 객체 -> json

    @PutMapping("/{id}")
    public Question update(@LoginUser User loginUser, @PathVariable long id, @RequestBody Question updateQuestion) {
        return qnaService.update(loginUser, id, updateQuestion);
    }
}
