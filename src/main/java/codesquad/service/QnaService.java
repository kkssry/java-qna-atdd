package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service("qnaService")  //서비스에서는 비즈니스 로직을 구현하는 곳이 아니다!! , thin layer - 가볍게 구현해야 한다.
//상태값을 가지는 곳은 domain이다.
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return questionRepository.findById(id).orElseThrow(UnAuthorizedException::new);
    }

    @Transactional
    public void update(User loginUser, long id, Question updatedQuestion) {
        questionRepository
                .findById(id)
                .filter(user -> user.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new)
                .modify(updatedQuestion, loginUser);

    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) {
        Question question = questionRepository
                .findById(questionId)
                .filter(writer -> writer.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new);

        if (!question.isDeleted()) {
            question.deleteQuestion(loginUser);
            questionRepository.save(question);
        }
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        // TODO 답변 추가 기능 구현
        return null;
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현 
        return null;
    }

    public void add(Question question) {
        questionRepository.save(question);
    }
}
