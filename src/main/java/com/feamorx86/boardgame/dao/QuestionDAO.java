package com.feamorx86.boardgame.dao;

import com.feamorx86.boardgame.model.*;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by Home on 25.08.2017.
 */
@Transactional
@Repository
public class QuestionDAO {
    @Autowired
    private SessionFactory sessionFactory;

    public Section getSection(int sectionId) {
        Section section = (Section) sessionFactory.getCurrentSession().createCriteria(Section.class).add(Restrictions.eq("id", sectionId)).uniqueResult();
        return section;
    }

    public List<Section> sectionsByPosition() {
        List<Section> sections = sessionFactory.getCurrentSession().createCriteria(Section.class).addOrder(Order.asc("position")).list();
        return sections;
    }

    public List<SubSection> getSubSectionsForSection(int sectionId) {
        List<SubSection> subSections = sessionFactory.getCurrentSession().createCriteria(SubSection.class).add(Restrictions.eq("sectionId", sectionId)).addOrder(Order.asc("position")).list();
        return subSections;
    }

//    public void deleteSubSection(SubSection subSection) {
//        sessionFactory.getCurrentSession().delete(subSection);
//    }

    public void deleteSubSectionWithQuestions(SubSection subSection) {
        List<Question> questions = listSubsectionQuestions(subSection.getId());
        for(Question q : questions) {
            deleteQuestionWithAnswers(q);
        }
        sessionFactory.getCurrentSession().delete(subSection);
    }

    public void deleteQuestionWithAnswers(Question question) {
        List<Answer> answers = sessionFactory.getCurrentSession().createCriteria(Answer.class).add(Restrictions.eq("questionId", question.getId())).list();
        List<CorrectAnswer> correctAnswers = sessionFactory.getCurrentSession().createCriteria(CorrectAnswer.class).add(Restrictions.eq("questionId", question.getId())).list();
        for(CorrectAnswer ca : correctAnswers) {
            sessionFactory.getCurrentSession().delete(ca);
        }
        if (answers != null && answers.size() > 0) {
            for (Answer a : answers) {
                deleteAnswerWithTextAndCorrectAnswers(a);
            }
        }
        TextData questionText = (TextData) sessionFactory.getCurrentSession().createCriteria(TextData.class).add(Restrictions.eq("id", question.getTextDataId())).uniqueResult();
        if (questionText != null) {
            sessionFactory.getCurrentSession().delete(questionText);
        }
        sessionFactory.getCurrentSession().delete(question);
    }

    public void deleteAnswerWithTextAndCorrectAnswers(Answer answer) {
        TextData answerText = (TextData) sessionFactory.getCurrentSession().createCriteria(TextData.class).add(Restrictions.eq("id", answer.getTextDataId())).uniqueResult();
        if (answerText != null) {
            sessionFactory.getCurrentSession().delete(answerText);
        }
        List<CorrectAnswer> results = sessionFactory.getCurrentSession().createCriteria(CorrectAnswer.class)
                .add(Restrictions.eq("answerId", answer.getId())).list();
        if (results != null && results.size() > 0) {
            for(CorrectAnswer ca : results) {
                sessionFactory.getCurrentSession().delete(ca);
            }
        }
        sessionFactory.getCurrentSession().delete(answer);
    }

//    public void deleteQuestion(Question question) {
//        sessionFactory.getCurrentSession().delete(question);
//    }

//    public void deleteAnswer(Answer answer) {
//        sessionFactory.getCurrentSession().delete(answer);
//    }

    public void deleteCorrectAnswer(CorrectAnswer correctAnswer) {
        sessionFactory.getCurrentSession().delete(correctAnswer);
    }

    public SubSection getSubSection(int subSectionId) {
        SubSection subSection = (SubSection) sessionFactory.getCurrentSession().createCriteria(SubSection.class).add(Restrictions.eq("id", subSectionId)).uniqueResult();
        return subSection;
    }

    public Question getQuestion(int questionId) {
        Question question = (Question) sessionFactory.getCurrentSession().createCriteria(Question.class).add(Restrictions.eq("id", questionId)).uniqueResult();
        return question;
    }

    public void addQuestion(Question question) {
        sessionFactory.getCurrentSession().save(question);
    }

    public void updateQuestion(Question question) {
        sessionFactory.getCurrentSession().update(question);
    }

    public Answer getAnswer(int answerId) {
        Answer answer = (Answer) sessionFactory.getCurrentSession().createCriteria(Answer.class).add(Restrictions.eq("id", answerId)).uniqueResult();
        return answer;
    }

    public CorrectAnswer getCorrectAnswer(int correctAnswerId) {
        CorrectAnswer correctAnswer = (CorrectAnswer) sessionFactory.getCurrentSession().createCriteria(CorrectAnswer.class).add(Restrictions.eq("id", correctAnswerId)).uniqueResult();
        return correctAnswer;
    }

    public List<Answer> listAnswers(int questionId) {
        List<Answer> results = sessionFactory.getCurrentSession().createCriteria(Answer.class).add(Restrictions.eq("questionId", questionId)).addOrder(Order.asc("position")).list();
        return results;
    }

    public List<CorrectAnswer> listCorrectAnswers(int questionId) {
        List<CorrectAnswer> results = sessionFactory.getCurrentSession().createCriteria(CorrectAnswer.class).add(Restrictions.eq("questionId", questionId)).addOrder(Order.asc("questionId")).list();
        return results;
    }

    public List<CorrectAnswer> listCorrectAnswersForAnswer(int answerId) {
        List<CorrectAnswer> results = sessionFactory.getCurrentSession().createCriteria(CorrectAnswer.class)
                .add(Restrictions.eq("answerId", answerId)).list();
        return results;
    }

    public List<Question> listSubsectionQuestions(int subSectionId) {
        List<Question> result = sessionFactory.getCurrentSession().createCriteria(Question.class).add(Restrictions.eq("subSectionId", subSectionId)).addOrder(Order.asc("id")).list();
        return result;
    }

    public void addSection(Section section) {
        sessionFactory.getCurrentSession().save(section);
    }

    public void updateSection(Section section) {
        sessionFactory.getCurrentSession().update(section);
    }

    public void deleteSection(Section section) {
        sessionFactory.getCurrentSession().delete(section);
    }

    public void addSubSection(SubSection subSection) {
        sessionFactory.getCurrentSession().save(subSection);
    }

    public void updateSubSection(SubSection subSection) {
        sessionFactory.getCurrentSession().update(subSection);
    }

    public void addAnswer(Answer answer) {
        sessionFactory.getCurrentSession().save(answer);
    }

    public void updateAnswer(Answer answer) {
        sessionFactory.getCurrentSession().update(answer);
    }

    public void addCorrectAnswer(CorrectAnswer correctAnswer) {
        sessionFactory.getCurrentSession().save(correctAnswer);
    }

    public void updateCorrectAnswer(CorrectAnswer correctAnswer) {
        sessionFactory.getCurrentSession().update(correctAnswer);
    }

    public boolean hasSectionWithId(int sectionId) {
        return sessionFactory.getCurrentSession().createSQLQuery("SELECT s.id FROM section as s WHERE s.id = :sec").setParameter("sec", sectionId).uniqueResult() != null;
    }

    public List<Integer> listSubSectionIds(int sectionId) {
        return sessionFactory.getCurrentSession().createSQLQuery("SELECT id FROM sub_section WHERE section_id = :sec").setParameter("sec", sectionId).list();
    }

    public List<Integer> listQuestionIds(int subSectionId) {
        return sessionFactory.getCurrentSession().createSQLQuery("SELECT id FROM question WHERE sub_section_id = :sec").setParameter("sec", subSectionId).list();
    }
}
