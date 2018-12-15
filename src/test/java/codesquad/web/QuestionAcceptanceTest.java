package codesquad.web;

import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm_no_login() throws Exception {
        ResponseEntity<String> response = template()
                .getForEntity("/questions/form", String.class); //입력값
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());

    }

    @Test
    public void createQuestion_login() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity("/questions/form",String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

//    @Test
//    public void create() throws Exception {
//        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
//                .addParameter("userId","testuser")
//                .addParameter("password","password")
//                .addParameter("name","자바지기")
//                .addParameter("email","javajigi@slipp.net")
//                .build();
//
//        ResponseEntity<String> response = template().postForEntity("/users", request, String.class);
//
//        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
//        softly.assertThat(userRepository.findByUserId("testuser").isPresent()).isTrue();
//        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/users");
//    }


    @Test
    public void create() throws Exception {
        User loginUser = defaultUser();
        HttpEntity<MultiValueMap<String,Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title","제목입니다.")
                .addParameter("contents","내용입니다.")
                .build();

//        ResponseEntity<String> response = basicAuthTemplate(loginUser)
//                .getForEntity("/questions/form",String.class);

        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/questions",request,String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findById(loginUser.getId()).isPresent()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }
}
