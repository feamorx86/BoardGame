package com.feamorx86.boardgame.model;

import javax.persistence.*;

/**
 * Created by Home on 25.08.2017.
 */
@Entity(name = "answer")
@Table(name = "answer")
public class Answer {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "text_data_id",nullable = false)
    private int textDataId;

    @Column(name = "question_id", nullable = false)
    private int questionId;

    @Column(name = "position", nullable = false)
    private int position;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTextDataId() {
        return textDataId;
    }

    public void setTextDataId(int textDataId) {
        this.textDataId = textDataId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
