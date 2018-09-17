package com.feamorx86.boardgame.model;

import org.springframework.util.StringUtils;

import javax.persistence.*;

/**
 * Created by Home on 25.08.2017.
 */
@Entity(name = "correct_answer")
@Table(name = "correct_answer")
public class CorrectAnswer {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "question_id", nullable = false)
    private int questionId;
    @Column(name = "answer_id", nullable = true)
    private Integer answerId;
    @Column(name = "int_result", nullable = true)
    private Integer intResult;
    @Column(name = "float_result", nullable = true)
    private Float floatResult;
    @Column(name = "str_result", nullable = true, length = 1000)
    private String strResult;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }

    public Integer getIntResult() {
        return intResult;
    }

    public void setIntResult(Integer intResult) {
        this.intResult = intResult;
    }

    public Float getFloatResult() {
        return floatResult;
    }

    public void setFloatResult(Float floatResult) {
        this.floatResult = floatResult;
    }

    public String getStrResult() {
        return strResult;
    }

    public void setStrResult(String strResult) {
        this.strResult = strResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CorrectAnswer that = (CorrectAnswer) o;

        if (questionId != that.questionId) return false;
        if (answerId != null ? !answerId.equals(that.answerId) : that.answerId != null) return false;
        if (intResult != null ? !intResult.equals(that.intResult) : that.intResult != null) return false;
        if (floatResult != null ? !floatResult.equals(that.floatResult) : that.floatResult != null) return false;
        return !StringUtils.isEmpty(strResult) ? strResult.equalsIgnoreCase(that.strResult) : StringUtils.isEmpty(that.strResult);
    }
}
