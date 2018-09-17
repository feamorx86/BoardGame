package com.feamorx86.boardgame.test;

import com.feamorx86.boardgame.Const;
import com.feamorx86.boardgame.dao.QuestionDAO;
import com.feamorx86.boardgame.dao.TextDAO;
import com.feamorx86.boardgame.dao.UserDAO;
import com.feamorx86.boardgame.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * Created by Home on 25.08.2017.
 */
public class FillTestData {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private TextDAO textDAO;
    @Autowired
    private QuestionDAO questionDAO;


    public void fillUsers() {

        UserModel user;

        user = new UserModel();
        user.setFirstName("Сергей");
        user.setSecondName("Svendrovskii");
        user.setThirdName("Alexandrovich");
        user.setLogin("feamor");
        user.setPassword("1979");
        user.setTag("root");
        user.setType(Const.UserTypes.ADMIN);
        userDAO.add(user);

        user = new UserModel();
        user.setFirstName("Александра");
        user.setSecondName("Филипповна");
        user.setThirdName("Свендровская");
        user.setLogin("a.svend");
        user.setPassword("1979");
        user.setTag("каф. Детали машин");
        user.setType(Const.UserTypes.TEACHER);
        userDAO.add(user);

        user = new UserModel();
        user.setFirstName("Коротаева");
        user.setSecondName("Елена");
        user.setTag("ИВТ-01");
        user.setType(Const.UserTypes.STUDENT);
        userDAO.add(user);

        user = new UserModel();
        user.setFirstName("Иванов");
        user.setSecondName("Иван");
        user.setTag("ИВТ-01");
        user.setType(Const.UserTypes.STUDENT);
        userDAO.add(user);
    }

    public void fillSections() {
        Section section;

        section = new Section();
        section.setTitle("Термодинамика");
        section.setPosition(0);
        section.setDescription("");
        section.setComment("тест для студентов");
        questionDAO.addSection(section);
        createSubSections(section);

        section= new Section();
        section.setPosition(1);
        section.setTitle("Химия");
        section.setDescription("");
        section.setComment("тест для даунов");
        questionDAO.addSection(section);
        createSubSections(section);
    }

    public void createSubSections(Section forSection) {
        SubSection sub;

        sub = new SubSection();
        sub.setPosition(0);
        sub.setTitle("Термины");
        sub.setSectionId(forSection.getId());
        sub.setComment("asdasdasdsad");
        questionDAO.addSubSection(sub);
        createQuestions(forSection, sub);

        sub = new SubSection();
        sub.setTitle("Задачи");
        sub.setPosition(1);
        sub.setSectionId(forSection.getId());
        questionDAO.addSubSection(sub);
        createQuestions(forSection, sub);
    }

    public TextData createText(String withText, int type) {
        TextData data = textDAO.create(type, withText);
        return data;
    }

    public Question createQuestion(int type, int textId, int sectionId, int subSectionId, float rate) {
        Question question = new Question();
        question.setType(type);
        question.setSectionId(sectionId);
        question.setSubSectionId(subSectionId);
        question.setTextDataId(textId);
        question.setRate(rate);
        questionDAO.addQuestion(question);
        return question;
    }

    public Answer createAnswer(int textId, int pos, int questionId) {
        Answer answer = new Answer();
        answer.setTextDataId(textId);
        answer.setPosition(pos);
        answer.setQuestionId(questionId);
        questionDAO.addAnswer(answer);
        return answer;
    }

    public CorrectAnswer createCorrect(int questionId, Integer answerIsIdWith, Float answerIsFloat, Integer answerIsInt, String answerIsStr) {
        CorrectAnswer correctAnswer = new CorrectAnswer();
        correctAnswer.setQuestionId(questionId);

        correctAnswer.setAnswerId(answerIsIdWith);
        correctAnswer.setIntResult(answerIsInt);
        correctAnswer.setFloatResult(answerIsFloat);
        correctAnswer.setStrResult(answerIsStr);

        int notNullCounter = 0;
        if (answerIsFloat != null) notNullCounter++;
        if (answerIsInt != null) notNullCounter++;
        if (answerIsStr != null) notNullCounter++;
        if (notNullCounter > 1) throw new RuntimeException("There is more then one of answer type! for question with "+questionId);

        questionDAO.addCorrectAnswer(correctAnswer);
        return correctAnswer;
    }

    public ArrayList<Question> createQuestions(Section section, SubSection subSection) {
        ArrayList<Question> questions = new ArrayList<Question>();
        Question question;
        Answer answer;
//        CorrectAnswer correctAnswer;

        question = createQuestion(Const.QuestionTypes.ONE_CORRECT, createText("2 x 2 = ?", Const.TextDataTypes.SIMPLE_TEXT).getId(), section.getId(), subSection.getId(), 1.0f);
        answer = createAnswer(createText("2", Const.TextDataTypes.SIMPLE_TEXT).getId(), 0, question.getId());
        createCorrect(question.getId(), answer.getId(), null, null, null);
        createAnswer(createText("4", Const.TextDataTypes.SIMPLE_TEXT).getId(), 1, question.getId());
        createAnswer(createText("8", Const.TextDataTypes.SIMPLE_TEXT).getId(), 2, question.getId());


        question = createQuestion(Const.QuestionTypes.MANY_CORRECT, createText("Укажите делители 6 кроме 6 и 1.", Const.TextDataTypes.SIMPLE_TEXT).getId(), section.getId(), subSection.getId(), 0.00102f);
        createAnswer(createText("1", Const.TextDataTypes.SIMPLE_TEXT).getId(), 0, question.getId());
        answer = createAnswer(createText("<p style=\"color:red\">2</p>", Const.TextDataTypes.WEB_VIEW).getId(), 1, question.getId());
        createCorrect(question.getId(), answer.getId(), null, null, null);
        answer = createAnswer(createText("3", Const.TextDataTypes.SIMPLE_TEXT).getId(), 2, question.getId());
        createCorrect(question.getId(), answer.getId(), null, null, null);
        createAnswer(createText("4", Const.TextDataTypes.SIMPLE_TEXT).getId(), 3, question.getId());

        question = createQuestion(Const.QuestionTypes.INT_INPUT, createText("47 + 17 = ?", Const.TextDataTypes.SIMPLE_TEXT).getId(), section.getId(), subSection.getId(), 10f);
        answer = createAnswer(createText("Ввежите ответ:", Const.TextDataTypes.SIMPLE_TEXT).getId(), 0, question.getId());
        createCorrect(question.getId(), answer.getId(), null, 64, null);

        question = createQuestion(Const.QuestionTypes.INT_INPUT, createText("<p>Решить систему уравнений</p>\n" +
                "<img src=\"https://chart.googleapis.com/chart?cht=tx&chl=\\left\\{\\begin{array}{l}3x%2B2y=29\\\\y\\left(2%2Bx\\right)=49\\end{array}\\right.\">", Const.TextDataTypes.WEB_VIEW).getId(), section.getId(), subSection.getId(), 0.5f);
        answer = createAnswer(createText("x = ", Const.TextDataTypes.SIMPLE_TEXT).getId(), 1, question.getId());
        createCorrect(question.getId(), answer.getId(), null, 5, null);
        answer = createAnswer(createText("y = ", Const.TextDataTypes.SIMPLE_TEXT).getId(), 2, question.getId());
        createCorrect(question.getId(), answer.getId(), null, 7, null);

        return questions;
    }
}
