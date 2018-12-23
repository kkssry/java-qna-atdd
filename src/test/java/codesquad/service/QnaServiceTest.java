package codesquad.service;

import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest extends BaseTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    @Test
    public void create_question() {
        Question question = new Question("제목입니다.", "내용입니다.");
        qnaService.create(UserTest.JAVAJIGI,question);
    }

    @Test (expected = UnAuthenticationException.class)
    public void create_question_no_login() {
        Question question = new Question("제목입니다.", "내용입니다.");
        qnaService.create(null,question);
    }

    @Test
    public void create_answer() {
        Question question = new Question("제목입니다.", "내용입니다.");
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        qnaService.addReply(UserTest.JAVAJIGI,question.getId(),"답변입니다.");
    }

    @Test (expected = UnAuthenticationException.class)
    public void create_answer_no_login() {
        Question question = new Question("제목입니다.", "내용입니다.");
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        qnaService.addReply(null,question.getId(),"답변입니다.");
    }

    @Test
    public void update_question() {
        Question question = new Question("제목입니다.", "내용입니다.");
        question.writtenBy(UserTest.JAVAJIGI);
        Question targetQuestion = new Question("업데이트 제목", "업데이트 내용");

        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        Question updatedQuestion = qnaService.update(UserTest.JAVAJIGI, question.getId(), targetQuestion);
        softly.assertThat(updatedQuestion.getTitle()).isEqualTo("업데이트 제목");
    }

    @Test(expected = UnAuthenticationException.class)
    public void update_question_no_login() {
        Question question = new Question("제목입니다.", "내용입니다.");
//
        Question targetQuestion = new Question("업데이트 제목", "업데이트 내용");

        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        Question updatedQuestion = qnaService.update(null, question.getId(), targetQuestion);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_question_other_user() {
        Question question = new Question("제목 입니다.", "내용입니다.");
        question.writtenBy(UserTest.JAVAJIGI);
        Question targetQuestion = new Question("업데이트 제목", "업데이트 내용");

        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        qnaService.update(UserTest.SANJIGI, question.getId(), targetQuestion);
    }

    @Test
    public void delete_answer() {
        Answer answer = new Answer(UserTest.JAVAJIGI, "내용입니다.");

        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));
        Answer deletedAnswer = qnaService.deleteAnswer(UserTest.JAVAJIGI, answer.getId());
        softly.assertThat(deletedAnswer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthenticationException.class)
    public void delete_answer_no_login() {
        Answer answer = new Answer(UserTest.JAVAJIGI, "내용입니다.");

        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));
        qnaService.deleteAnswer(null, answer.getId());
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_answer_other_question() {
        Answer answer = new Answer(UserTest.JAVAJIGI, "내용입니다.");

        when(answerRepository.findById(answer.getId())).thenReturn(Optional.of(answer));
        qnaService.deleteAnswer(UserTest.SANJIGI, answer.getId());
    }
}
