package qna.domain;

import qna.exception.CannotDeleteException;
import qna.exception.UnAuthorizedException;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Lob
    private String contents;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(length = 100, nullable = false)
    private String title;

    @Column
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name="fk_question_writer"), name = "writer_id")
    private User writer;

    @OneToMany(mappedBy = "question")
    private List<Answer> answers = new ArrayList<Answer>();

    public Question() {
    }

    public Question(User writer, String title, String contents) {
        this(null, writer, title, contents);
    }

    public Question(Long id, User writer, String title, String contents) {
        this.id = id;

        if (Objects.isNull(writer)) {
            throw new UnAuthorizedException();
        }

        this.title = title;
        this.contents = contents;
    }

    public boolean isOwner(User writer) {
        return this.writer.isEqual(writer);
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public boolean possibleDelete() {
        boolean onlyOwn = true;
        for (Answer answer : answers) {
            onlyOwn = onlyOwn && answer.isOwner(writer);
        }
        return onlyOwn;
    }

    public Long getId() {
        return id;
    }

    public String getContents() {
        return contents;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<DeleteHistory> delete() {
        this.deleted = true;
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        deleteHistories.add(DeleteHistory.ofQuestion(this.id, this.writer));
        for (Answer answer : answers) {
            deleteHistories.add(answer.delete());
        }
        return deleteHistories;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public User getWriter() {
        return writer;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", writerId=" + writer +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deleted=" + deleted +
                '}';
    }

    public void toDelete(LocalDateTime deleteAt) throws CannotDeleteException {
        if (this.deleted) {
            throw new CannotDeleteException("이미 삭제된 답변입니다.");
        }
        this.deleted = true;
        this.updatedAt = deleteAt;
    }
}
