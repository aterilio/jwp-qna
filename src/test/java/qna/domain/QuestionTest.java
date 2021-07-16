package qna.domain;

import org.junit.jupiter.api.Test;
import qna.exception.CannotDeleteException;
import qna.exception.UnAuthorizedException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class QuestionTest {

    public static final Question Q1 = new Question(UserTest.JAVAJIGI, "title1", "contents1");
    public static final Question Q2 = new Question(UserTest.SANJIGI, "title2", "contents2");

    @Test
    public void create_success() {
        Question question = new Question(UserTest.JAVAJIGI, "title", "content");
        assertThat(question).isNotNull();
    }

    @Test
    public void create_failedByUser() {
        assertThatThrownBy(() -> {
            new Question(null, "title", "content");
        }).isInstanceOf(UnAuthorizedException.class);
    }

    @Test
    public void update_delete_success() throws CannotDeleteException {
        Question question = new Question(0L, UserTest.JAVAJIGI, "title", "content");
        assertThat(question.isDeleted()).isFalse();
        assertThat(question.getUpdatedAt()).isNull();

        question.toDelete(LocalDateTime.now());
        assertThat(question.isDeleted()).isTrue();
        assertThat(question.getUpdatedAt()).isNotNull();
    }

    @Test
    public void update_delete_failedByAlreadyDeleted() throws CannotDeleteException {
        Question question = new Question(0L, UserTest.JAVAJIGI, "title", "content");
        question.toDelete(LocalDateTime.now());
        assertThatThrownBy(() -> {
            question.toDelete(LocalDateTime.now());
        }).isInstanceOf(CannotDeleteException.class);
    }
}
