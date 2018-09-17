package com.feamorx86.boardgame.test;

import com.feamorx86.boardgame.Const;
import com.feamorx86.boardgame.dao.QuestionDAO;
import com.feamorx86.boardgame.dao.TextDAO;
import com.feamorx86.boardgame.model.*;
import com.feamorx86.boardgame.page.BasePage;
import com.feamorx86.boardgame.utils.HTMLWriter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by Home on 28.08.2017.
 */
public class TestDisplayQuestion extends BasePage {

    @Autowired
    private TextDAO textDAO;
    @Autowired
    private QuestionDAO questionDAO;

    @Override
    public String getName() {
        return "test_question";
    }
/*
    @Override
    public Object render(HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException, NoSuchMethodException {
        super.render(request, response, user);
        HTMLWriter writer = new HTMLWriter();
        writer.startPage().title("тестовый просмотр вопросов");
        Integer id = getParameter("id", request);
        if (id != null) {
            Question question = questionDAO.getQuestion(id.intValue());
            if (question != null) {
                List<Answer> answers = questionDAO.listAnswers(question.getId());
                List<CorrectAnswer> correctAnswers = questionDAO.listCorrectAnswers(question.getId());

                displayQuestion(writer, question, answers, correctAnswers);
            } else {
                writer.writeln("Вопрос не найден = "+id);
            }
        } else {
            writer.writeln("Укажите вопрос");
        }
        writer.endPage();
        response.getOutputStream().print(writer.toString());
        return null;
    }

    private void displayText(HTMLWriter writer, int id) {
        TextData text = textDAO.get(id);
        BasePage.printTextData(text, writer);
    }

    private void displayQuestion(HTMLWriter writer, Question question, List<Answer> answers, List<CorrectAnswer> correctAnswers) {
        writer.ln()
                .push("div");
        writer.push("h3").appendHtml("Вопрос #"+question.getId()).pop();

        displayText(writer, question.getTextDataId());

        switch (question.getType()) {
            case Const.QuestionTypes.ONE_CORRECT:
                writer.startForm("answer", "post");
                for (Answer answer : answers) {
                    writer.startInput("radio", "answer", Integer.toString(answer.getId()), null)
                            .push("div");
                    displayText(writer, answer.getTextDataId());
                    writer.pop().endInput();
                }
                writer.submit("Ответить").endForm();
                break;
            case Const.QuestionTypes.MANY_CORRECT:
                writer.startForm("answer", "post");
                for (Answer answer : answers) {
                    writer.startInput("checkbox", "answer", Integer.toString(answer.getId()), null)
                            .push("div");
                    displayText(writer, answer.getTextDataId());
                    writer.pop().endInput();
                }
                writer.submit("Ответить").endForm();
                break;
            case Const.QuestionTypes.INT_INPUT:
            case Const.QuestionTypes.FLOAT_INPUT:
            case Const.QuestionTypes.TEXT_INPUT:
                writer.startForm("answer", "post");
                for (Answer answer : answers) {
                    writer.push("div");
                    displayText(writer, answer.getTextDataId());
                    writer.input(null, "answer", null, null, null);
                    writer.pop();
                }
                writer.submit("Ответить").endForm();
                break;
        }

        writer.ln();
        writer.writeln("Правильные ответы");
        for(CorrectAnswer c : correctAnswers) {
            if (c.getIntResult() != null) {
                Answer a = questionDAO.getAnswer(c.getAnswerId());
                displayText(writer, a.getTextDataId());
                writer.writeln(Integer.toString(c.getIntResult()));
            } else if (c.getFloatResult() != null) {
                Answer a = questionDAO.getAnswer(c.getAnswerId());
                displayText(writer, a.getTextDataId());
                writer.writeln(Float.toString(c.getFloatResult()));
            } else if (c.getStrResult() != null) {
                Answer a = questionDAO.getAnswer(c.getAnswerId());
                displayText(writer, a.getTextDataId());
                writer.writeln(c.getStrResult());
            } else {
                Answer a = questionDAO.getAnswer(c.getAnswerId());
                displayText(writer, a.getTextDataId());
            }
        }
        writer.pop();

    }*/
}
