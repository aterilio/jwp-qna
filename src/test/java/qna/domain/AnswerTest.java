package qna.domain;

import org.junit.jupiter.api.Test;
import qna.exception.NotFoundException;
import qna.exception.UnAuthorizedException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AnswerTest {

    public static final Answer A1 = new Answer(UserTest.JAVAJIGI, QuestionTest.Q1, "Answers Contents1");
    public static final Answer A2 = new Answer(UserTest.SANJIGI, QuestionTest.Q1, "Answers Contents2");

    @Test
    public void create_success() {
        Answer answer = new Answer(UserTest.JAVAJIGI, QuestionTest.Q1, "test");
        assertThat(answer).isNotNull();
    }

    @Test
    public void create_failedByUser() {
        assertThatThrownBy(() -> {
            new Answer(null, QuestionTest.Q1, "test");
        }).isInstanceOf(UnAuthorizedException.class);
    }

    @Test
    public void create_failedByQuestion() {
        assertThatThrownBy(() -> {
            new Answer(UserTest.JAVAJIGI, null, "test");
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void update_delete_success() {
        Answer answer = new Answer(0L, UserTest.JAVAJIGI, QuestionTest.Q1, "test");
        assertThat(answer.isDeleted()).isFalse();
        assertThat(answer.getUpdatedAt()).isNull();

        answer.toDelete(LocalDateTime.now());
        assertThat(answer.isDeleted()).isTrue();
        assertThat(answer.getUpdatedAt()).isNotNull();
    }
}
