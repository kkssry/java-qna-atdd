package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.UserTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.slf4j.LoggerFactory.getLogger;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = getLogger(ApiAnswerAcceptanceTest.class);

    @Test
    public void create() {
        String location = createResource("/api/questions", createQuestion());
        Answer newAnswer = new Answer(UserTest.SANJIGI, "댓글입니다.");
        ResponseEntity<Void> answerResponse = basicAuthTemplate(findByUserId("sanjigi"))
                .postForEntity(location + "/answers", newAnswer.getContents(), Void.class);
        log.debug("answerResponse: {}", answerResponse);

        softly.assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void delete() {
        String location = createResource("/api/questions", createQuestion());
        Question question = getResource(location, Question.class, UserTest.JAVAJIGI);
        Answer answer = new Answer(1L, UserTest.SANJIGI, question, "댓글입니다.");
        log.debug("answer : {}" , answer);
        ResponseEntity<Answer> answerResponse = basicAuthTemplate(findByUserId("sanjigi"))
                .exchange(location + "/answers/" + answer.getId(), HttpMethod.DELETE, createHttpEntity(answer), Answer.class);
        log.debug("answerResponse : {}", answerResponse);
        softly.assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(answer.isDeleted()).isTrue();
    }
}
