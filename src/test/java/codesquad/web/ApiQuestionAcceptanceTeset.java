package codesquad.web;

import codesquad.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static org.slf4j.LoggerFactory.getLogger;

public class ApiQuestionAcceptanceTeset extends AcceptanceTest {
    private static final Logger log = getLogger(ApiQuestionAcceptanceTeset.class);

    @Test
    public void create() {
        Question question = new Question("제목입니다.", "내용물입니다.");

        ResponseEntity<Void> response = basicAuthTemplate(findByUserId("sanjigi")).postForEntity("/api/questions", question, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();
        log.debug("response : {}", response);
        log.debug("location : {}", location);

        Question dbQuestion = template().getForObject(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();
        log.debug("dbQuestion : {}", dbQuestion);
    }

    @Test
    public void craete_no_login() {
        Question question = new Question("제목입니다.", "내용물입니다.");

        ResponseEntity<Void> response = template().postForEntity("/api/questions", question, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update() {
        Question question = new Question("제목입니다.", "내용입니다.");
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", question, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);      //질문 만듦

        Question original = basicAuthTemplate().getForObject(location, Question.class); //만든 질문
        Question updateQuestion = original.modify(original.setTitleAndContents("업데이트 제목", "업데이트 내용"), findByUserId("javajigi"));

        ResponseEntity<Question> responseEntity
                = basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateQuestion.equalsWriter(responseEntity.getBody())).isTrue();

    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }

    @Test
    public void update_other_user() {
        Question question = new Question("제목입니다.", "내용입니다.");
        ResponseEntity<Void> response = basicAuthTemplate(findByUserId("sanjigi")).postForEntity("/api/questions", question, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);      //질문 만듦

        Question updateQuestion = new Question("업데이트 질문","업데이트 내용");

        ResponseEntity<Void> responseEntity
                = basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_no_login() {
        Question question = new Question("제목입니다.", "내용입니다.");
        ResponseEntity<Void> response = basicAuthTemplate(findByUserId("sanjigi")).postForEntity("/api/questions", question, Void.class);
        String location = response.getHeaders().getLocation().getPath();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);      //질문 만듦

        Question updateQuestion = new Question("업데이트 질문","업데이트 내용");

        ResponseEntity<Void> responseEntity
                = template().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    
}
