package com.feamorx86.boardgame.page;

import com.feamorx86.boardgame.Const;
import com.feamorx86.boardgame.dao.QuestionDAO;
import com.feamorx86.boardgame.model.*;
import com.feamorx86.boardgame.utils.HTMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Home on 31.08.2017.
 */
public class EditQuestionsPage extends BasePage {

    @Autowired
    private QuestionDAO questionDAO;

    @Override
    public String getName() {
        return "edit_questions";
    }
/*
    @Override
    protected Object displayPage(HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        HTMLWriter writer = startPage();
        writer.title("Редкатор Вопросов")
                .subtitle("Разделы");
        List<Section> sections = questionDAO.sectionsByPosition();
        HashMap<Integer, List<SubSection>> subSectionsBySection = new HashMap<>();
        for(Section s : sections) {
            subSectionsBySection.put(s.getId(), questionDAO.getSubSectionsForSection(s.getId()));
        }
        writer.startTable("Раздел", "Подраздел", "Вопросы");
        for(Section section : sections) {
            writer.tableLine(section.getTitle() == null ? "" : section.getTitle(), "", "");
            List<SubSection> subSections = subSectionsBySection.get(section.getId());
            if (subSections != null) {
                for(SubSection subSection : subSections) {
                    writer.tableLine("", subSection.getTitle() == null ? "" : subSection.getTitle(),
                            HTMLWriter.makeRefTag("вопросы", "./edit_questions?action=list&sub_section="+subSection.getId()+"&section="+section.getId()));
                }
            }
        }
        writer
                .endTable()
                .refline("На главную", "./start");
        endPageAndSend(writer, response);
        return null;
    }

    @Override
    protected Object displayAction(String action, HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        switch(action) {
            case "new" : {
                Section section = getSectionFromRequest(request);
                SubSection subSection = getSubSectionFromRequest(request);
                if (section == null || subSection == null ) {
                    showError(request, response, "Идентификатор Раздела или подраздела не указан либо указан не верно.", "./edit_questions");
                } else {
                    int questionId = createNewQuestion(section, subSection);
                    String redirect;
                    redirect = "./edit_questions?action=edit&question=" + questionId;
                    return sendRedirect(redirect);
                }
            }
            break;
            case "edit": {
                Question question = getQuestionFromRequest(request);
//                if (question == null) {
//                    showError(request, response, "Идентификатор Вопроса не указан либо указан не верно.", "./edit_questions");
//                } else {
//                    displayEditQuestion(request, response, question, null, null);
//                }
                return sendRedirect("./question_editor?question="+request.getParameter("question"));
            }
            //break;
            case "delete" : {
                Question question = getQuestionFromRequest(request);
                if (question == null) {
                    showError(request, response, "Идентификатор Вопроса не указан либо указан не верно.", "./edit_questions");
                } else {
                    displayQuestionToDelete(response, question);
                }
            }
            break;
            case "list" : {
                Section section = getSectionFromRequest(request);
                SubSection subSection = getSubSectionFromRequest(request);
                if (section == null || subSection == null) {
                    showError(request, response, "Раздел или подраздел не указан либо указан не верно.", "./edit_questions");
                } else {
                    List<Question> questions = questionDAO.listSubsectionQuestions(subSection.getId());
                    displayQuestionsList(request, response, section, subSection, questions);
                }
            }
            break;
            case "search" : {
//                SubSection subSectionToDelete = getSubSectionFromRequest(request);
//                if (subSectionToDelete == null) {
//                    showError(request, response, "Идентификатор Подраздела не указан либо указан не верно.");
//                } else {
//                    displaySubSectionToDelete(request, response, subSectionToDelete);
//                }
            }
            break;
            case "edit_text_for_question" : {
                Question question = getQuestionFromRequest(request);
                if (question != null) {
                    TextData textData = textDAO.get(question.getTextDataId());
                    Integer textDataId = getParameter("text_id", request);
                    if (textData == null || textDataId == null || textData.getId() != textDataId.intValue()) {
                        showError(request, response, "Идентификатор Текста Вопроса не указан или не указан или не совпадает с идентификатором из запроса.", "./edit_questions");
                    } else {
                        displayEditQuestionText(question, textData, response, null, null);
                    }
                } else {
                    showError(request, response, "Идентификатор Вопроса не указан либо вопрос не найден.", "./edit_questions");
                }
            }
            break;

            case "add_answer" : {
                Question question = getQuestionFromRequest(request);
                if (question == null) {
                    showError(request, response, "Идентификатор Вопроса или не указан вопрос не найден .", "./edit_questions");
                } else {
                    int answerId = createNewAnswer(question);
                    String redirect = "./edit_questions?action=edit_answer&question=" + question.getId()+"&answer="+answerId;
                    return sendRedirect(redirect);
                }
            }
            break;
            case "edit_answer" : {
                Answer answer = getAnswerFromRequest(request);
                Question question = getQuestionFromRequest(request);
                if (answer != null) {
                    TextData answerText = textDAO.get(answer.getTextDataId());
                    if (answerText == null || question == null) {
                        showError(request, response, "Вопрос или текст ответа не заданы или не найдены", "./edit_questions");
                    } else {
                        displayEditAnswer(answer, question, answerText, response, null, null);
                    }
                } else {
                    showError(request, response, "Идентификатор Ответа не указан либо вопрос не найден.", "./edit_questions");
                }
            }
            break;
            case "delete_answer" : {
                Answer answer = getAnswerFromRequest(request);
                Integer questionId = getParameter("question", request);
                if (answer == null || questionId == null) {
                    showError(request, response, "Идентификатор Вопроса или Ответа не указан либо указан не верно.", "./edit_questions");
                } else {
                    displayAnswerToDelete(response, questionId, answer);
                }
            }
            break;
            case "add_correct_answer" : {
                Question question = getQuestionFromRequest(request);
                if (question == null) {
                    showError(request, response, "Идентификатор Вопроса или не указан вопрос не найден .", "./edit_questions");
                } else {
                    int answerId = createNewCorrectAnswer(question);
                    String redirect;
                    redirect = "./edit_questions?action=edit_correct_answer&question=" + question.getId()+"&correct_answer="+answerId;
                    return sendRedirect(redirect);
                }
            }
            break;
            case "edit_correct_answer" : {
                CorrectAnswer correctAnswer = getCorrectAnswerFromRequest(request);
                Question question = getQuestionFromRequest(request);
                if (correctAnswer != null && question!= null) {
                    displayEditCorrectAnswer(correctAnswer, question, response, null, null);
                } else {
                    showError(request, response, "Идентификатор Правильного ответа или Вопросап не указан либо вопрос не найден.", "./edit_questions");
                }
            }
            break;
            case "delete_correct_answer" : {
                CorrectAnswer correctAnswer = getCorrectAnswerFromRequest(request);
                Integer questionId = getParameter("question", request);
                if (correctAnswer == null || questionId == null) {
                    showError(request, response, "Идентификатор Вопроса или Правильного ответа не указан либо указан не верно.", "./edit_questions");
                } else {
                    displayCorrectAnswerToDelete(response, questionId, correctAnswer);
                }
            }
            break;
        }
        return null;
    }

    private void displayEditQuestion(HttpServletRequest request, HttpServletResponse response, Question questionToEdit, String message, String color) throws IOException {
        HTMLWriter writer = startPage();
        writer.title("Редкатор Вопросов");

        Section section = questionDAO.getSection(questionToEdit.getSectionId());
        SubSection subSection = questionDAO.getSubSection(questionToEdit.getSubSectionId());
        TextData textData = textDAO.get(questionToEdit.getTextDataId());

        if (section != null && subSection != null) {
            writer.writeln("Раздел : " + section.getTitle())
                    .writeln("Подраздел : " + subSection.getTitle())
                    .refline("К вопросам подраздела", "./edit_questions?action=list&sub_section="+subSection.getId()+"&section="+section.getId());
        } else {
            showError(request, response, "Ошибка: раздел или подраздел не верно", "./edit_questions");
            return;
        }
        if (!StringUtils.isEmpty(message)) {
            writer.writeln(message, "style=\"color:"+color+"\" ");
        }

        writer.title("Редактирование вопроса")
                .subtitle("Текст вопроса");

        writer.appendHtml("<div >");
        printTextData(textData, writer);
        writer.appendHtml("</div>");

        writer.refline("Изменить текст ответа", "./edit_questions?action=edit_text_for_question&question="+questionToEdit.getId()+"&text_id="+textData.getId());
        //print answers
        List<Answer> answers = questionDAO.listAnswers(questionToEdit.getId());
        HashMap<Integer, Answer> answerHashMap = new HashMap<>();
        writer.subtitle("Ответы на вопрос");
        if (answers != null && answers.size() > 0) {
            for(int i = 0; i < answers.size(); i++) {
                Answer answer = answers.get(i);
                answerHashMap.put(answer.getId(), answer);
                writer
                        .appendHtml("<div>")
                        .writeln("Ответ № "+answer.getPosition()+", ("+answer.getId()+")")
                        .refline("Изменить ответ", "./edit_questions?action=edit_answer&answer="+answer.getId()+"&question="+questionToEdit.getId())
                        .refline("Удалить Ответ", "./edit_questions?action=delete_answer&answer="+answer.getId()+"&question="+questionToEdit.getId())
                        .writeln("текст: ");
                TextData answerText = textDAO.get(answer.getTextDataId());
                printTextData(answerText, writer);
                writer.appendHtml("</div>");
            }
        }
        writer.refline("Добавить ответ", "./edit_questions?action=add_answer&question="+questionToEdit.getId());

        //print correct answers
        List<CorrectAnswer> correctAnswers = questionDAO.listCorrectAnswers(questionToEdit.getId());
        writer.subtitle("Правильные ответы");
        if (correctAnswers != null && correctAnswers.size() > 0) {
            for (int i = 0; i < correctAnswers.size(); i++) {
                CorrectAnswer correctAnswer = correctAnswers.get(i);
                Answer answer = answerHashMap.get(correctAnswer.getAnswerId());
                writer
                        .appendHtml("<div>")
                        .writeln("Ответ Id : "+correctAnswer.getId())
                        .refline("Изменить ответ", "./edit_questions?action=edit_correct_answer&correct_answer="+correctAnswer.getId()+"&question="+questionToEdit.getId())
                        .refline("Удалить ответ", "./edit_questions?action=delete_correct_answer&correct_answer="+correctAnswer.getId()+"&question="+questionToEdit.getId());
                if (answer != null) {
                    writer.writeln("Ответ № " + answer.getPosition());
                } else {
                    writer.writeln("< нет связанного ответа >");
                }
                if (correctAnswer.getStrResult() != null) {
                    writer.appendHtml("Строка: "+correctAnswer.getStrResult());
                }
                if (correctAnswer.getIntResult() != null) {
                    writer.appendHtml("Число: "+correctAnswer.getIntResult());
                }
                if (correctAnswer.getFloatResult() != null) {
                    writer.appendHtml("Дробь: "+correctAnswer.getFloatResult());
                }
                writer.appendHtml("</div>");
            }

        } else {
            writer.writeln(" - Правильных ответов Нет - ");
        }
        writer.refline("Добавить ответ", "./edit_questions?action=add_correct_answer&question="+questionToEdit.getId());


        writer.startForm("./edit_questions?action=edit&question="+questionToEdit.getId(), "POST");
        writer.writeln("Сложнасть вопроса (можно дробью 7.2, можно не заполнять)");
        if (questionToEdit.getRate() != null) {
            writer.input(null, "rate", questionToEdit.getRate().toString(), null, null);
        } else {
            writer.input(null, "rate", null, null, null);
        }

        writer.writeln("Тип вопроса :")
                .select(1, "type",
                        new String[]{Integer.toString(Const.QuestionTypes.ONE_CORRECT), Integer.toString(Const.QuestionTypes.MANY_CORRECT), Integer.toString(Const.QuestionTypes.INT_INPUT),
                                Integer.toString(Const.QuestionTypes.FLOAT_INPUT), Integer.toString(Const.QuestionTypes.TEXT_INPUT)},
                        new String[]{"Один верный ответ", "Несколько верных ответов", "Ответ - целое число", "Ответ - дробное число", "Ответ - текст"},
                        Integer.toString(questionToEdit.getType()));
        writer.ln().submit("Сохранить");
        endPageAndSend(writer, response);
    }


    private void displayQuestionToDelete(HttpServletResponse response, Question questionToDelete) throws IOException {
        HTMLWriter writer = startPage();
        writer
                .title("Редкатор Вопросов")
                .subtitle("Вы действительно хотите удалить вопроси все связанные с ним ответы?");
        TextData textData = textDAO.get(questionToDelete.getTextDataId());
        writer.writeln("Текст ответа");
        writer.appendHtml("<div >");
        if (textData != null) {
            printTextData(textData, writer);
        } else {
            writer.writeln("< Текст Вопроса не задан >");
        }
        writer.appendHtml("</div>");
        //print answers
        List<Answer> answers = questionDAO.listAnswers(questionToDelete.getId());
        HashMap<Integer, Answer> answerHashMap = new HashMap<>();
        writer.writeln("Ответы на вопрос").appendHtml("<div>");
        if (answers != null && answers.size() > 0) {
            for(int i = 0; i < answers.size(); i++) {
                Answer answer = answers.get(i);
                answerHashMap.put(answer.getId(), answer);
                writer.appendHtml("<div>").writeln("Ответ № "+answer.getPosition()+", ("+answer.getId()+")")
                        .writeln("текст: ");
                TextData answerText = textDAO.get(answer.getTextDataId());
                if (answerText != null) {
                    printTextData(answerText, writer);
                } else {
                    writer.writeln("< Текст ответа не задан>");
                }
                writer.appendHtml("</div>");
            }
        }
        writer.appendHtml("</div>");
        //print correct answers
        List<CorrectAnswer> correctAnswers = questionDAO.listCorrectAnswers(questionToDelete.getId());
        writer.writeln("Правильные ответы").appendHtml("<div>");
        if (correctAnswers != null && correctAnswers.size() > 0) {
            for (int i = 0; i < correctAnswers.size(); i++) {
                writer.appendHtml("<div>");
                CorrectAnswer correctAnswer = correctAnswers.get(i);
                Answer answer = answerHashMap.get(correctAnswer.getAnswerId());
                if (answer != null) {
                    writer.writeln("Ответ Id : " + correctAnswer.getId())
                            .writeln("Ответ № " + answer.getPosition());
                    if (correctAnswer.getStrResult() != null) {
                        writer.appendHtml("Строка: " + correctAnswer.getStrResult());
                    }
                    if (correctAnswer.getIntResult() != null) {
                        writer.appendHtml("Число: " + correctAnswer.getIntResult());
                    }
                    if (correctAnswer.getFloatResult() != null) {
                        writer.appendHtml("Дробь: " + correctAnswer.getFloatResult());
                    }
                } else {
                    writer.writeln("< Правильный ответ не задан >");
                }
                writer.appendHtml("</div>");
            }
        } else {
            writer.writeln(" < Правильных ответов Нет > ");
        }
        writer.appendHtml("</div>")
                .startForm("./edit_questions?action=delete&question="+questionToDelete.getId(), "POST")
                .submit("Удалить")
                .endForm()
                .refline("Назад", "./edit_questions?action=list&sub_section="+questionToDelete.getSubSectionId()+"&section="+questionToDelete.getSectionId());
        endPageAndSend(writer, response);
    }

    private void displayQuestionsList(HttpServletRequest request, HttpServletResponse response, Section section, SubSection subSection, List<Question> questions)  throws IOException {
        HTMLWriter writer = startPage();
        writer.title("Редактор вопросов")
                .subtitle("Список вопросов из ")
                .writeln("Раздела : "+section.getTitle())
                .writeln("Подраздела :"+subSection.getTitle())
                .refline("к разделам", "./edit_questions")
                .subtitle("Вопросы : ");
        printQuestions(writer, questions);
        writer.refline("Добавить вопрос", "./edit_questions?action=new&section="+section.getId()+"&sub_section="+subSection.getId());
        endPageAndSend(writer, response);
    }

    private void printQuestions(HTMLWriter writer, List<Question> questions)  throws IOException {
        writer.startTable("ID", "Сложность", "Тип", "Текст", "Имзенить", "Удалить");
        for(Question question : questions) {
            TextData text = textDAO.get(question.getTextDataId());
            writer.openTableLine("#"+question.getId(), question.getRate() == null ? "" : question.getRate().toString(), getQuestionTypeShort(question.getType()));
            writer.push("td");
            printTextData(text, writer);
            writer
                    .pop()
                    .push("td").ref("Изменить", "./edit_questions?action=edit&question=" + question.getId()).pop()
                    .push("td").ref("Удалить", "./edit_questions?action=delete&question=" + question.getId()).pop()
                    .closeTableLine();
        }
        writer.endTable();
    }


    private void displayEditQuestionText(Question question, TextData textData, HttpServletResponse response, String message, String color) throws IOException {
        HTMLWriter writer = startPage();
        writer.title("Редактор вопросов").subtitle("Редактирование текста (#"+textData.getId()+") для вопроса #"+ question.getId());

        if (!StringUtils.isEmpty(message)) {
            writer.writeln(message, "style=\"color:"+color+"\" ");
        }

        writer.startForm("./edit_questions?action=edit_text_for_question&question="+question.getId()+"&text_id="+textData.getId(), "POST")
                .writeln("Выберете тип текста")
                .select(1, "type", Const.TextDataTypes.listValues(), Const.TextDataTypes.listNames(), Integer.toString(textData.getType()))
                .writeln("Изменить текст ")
                .textArea("text", true, false, 100, 20, null, textData.getText())
                .refline("Назад", "./edit_questions?action=edit&question=" + question.getId())
                .submit("Сохранить");
        writer.endForm();
        endPageAndSend(writer, response);
    }

    private void displayEditAnswer(Answer answer, Question question, TextData answerText, HttpServletResponse response, String message, String color) throws IOException {
        HTMLWriter writer = startPage();
        writer.title("Редактор вопросов").subtitle("Редактирование Ответа (#"+answer.getId()+") для вопроса #"+ question.getId());

        if (!StringUtils.isEmpty(message)) {
            writer.writeln(message, "style=\"color:"+color+"\" ");
        }

        writer.startForm("./edit_questions?action=edit_answer&question="+question.getId()+"&answer="+answer.getId()+"&text_id="+answerText.getId(), "POST")
                .writeln("Порядковый номер ответа")
                .input(null, "position", Integer.toString(answer.getPosition()), null, null)
                .writeln("Тип текста")
                .select(1, "type", Const.TextDataTypes.listValues(), Const.TextDataTypes.listNames(), Integer.toString(answerText.getType()))
                .writeln("Изменить текст ")
                .textArea("text", true, false, 100, 20, null, answerText.getText())
                .refline("Назад", "./edit_questions?action=edit&question=" + question.getId())
                .submit("Сохранить");
        writer.endForm();
        endPageAndSend(writer, response);
    }

    private void displayAnswerToDelete(HttpServletResponse response, int questionId, Answer answerToDelete) throws IOException {
        HTMLWriter writer = startPage();
        writer
                .title("Редактор вопросов")
                .subtitle("Вы действительно хотите удалить ответ и все связанные с ним подазделы правильные ответы?");

        TextData textData = textDAO.get(answerToDelete.getTextDataId());
        writer.writeln("Текст ответа: ");
        writer.appendHtml("<div >");
        if (textData != null) {
            printTextData(textData, writer);
        } else {
            writer.writeln("< Текст Вопроса не задан >");
        }
        writer.appendHtml("</div>");
        List<CorrectAnswer> correctAnswers = questionDAO.listCorrectAnswersForAnswer(answerToDelete.getId());
        writer.writeln("Правильные ответы : ").appendHtml("<div>");
        if (correctAnswers != null && correctAnswers.size() > 0) {
            for (int i = 0; i < correctAnswers.size(); i++) {
                writer.appendHtml("<div>");
                CorrectAnswer correctAnswer = correctAnswers.get(i);
                writer.writeln("Ответ Id : " + correctAnswer.getId());
                if (correctAnswer.getStrResult() != null) {
                    writer.appendHtml("Строка: " + correctAnswer.getStrResult());
                }
                if (correctAnswer.getIntResult() != null) {
                    writer.appendHtml("Число: " + correctAnswer.getIntResult());
                }
                if (correctAnswer.getFloatResult() != null) {
                    writer.appendHtml("Дробь: " + correctAnswer.getFloatResult());
                }
                writer.appendHtml("</div>");
            }
        } else {
            writer.writeln(" < Правильных ответов Нет > ");
        }
        writer.appendHtml("</div>")
                .startForm("./edit_questions?action=delete_answer&question="+questionId+"&answer="+answerToDelete.getId(), "POST")
                .submit("Удалить")
                .endForm()
                .refline("Назад", "./edit_questions?action=edit&question="+questionId);
        endPageAndSend(writer, response);
    }

    private void displayEditCorrectAnswer(CorrectAnswer correctAnswer, Question question, HttpServletResponse response, String message, String color) throws IOException {
        HTMLWriter writer = startPage();
        writer.title("Редактор вопросов").subtitle("Редактирование Правильного ответа (#"+correctAnswer.getId()+") для вопроса #"+ question.getId());

        if (!StringUtils.isEmpty(message)) {
            writer.writeln(message, "style=\"color:"+color+"\" ");
        }

        writer.startForm("./edit_questions?action=edit_correct_answer&question="+question.getId()+"&correct_answer="+correctAnswer.getId(), "POST")
                .writeln("К какому ответу относится этот Правильный ответ");
        List<Answer> answers = questionDAO.listAnswers(question.getId());
        if (answers.size() > 0) {
            String[] answerText = new String[answers.size()];
            String[] answerIds = new String[answers.size()];
            String selectedAnswer = "";
            for(int i=0; i< answers.size(); i++) {
                Answer a = answers.get(i);
                TextData text = textDAO.get(a.getTextDataId());
                if (text != null && text.getText() != null) {
                    answerText[i] = "#"+a.getId()+" "+text.getText();
                } else {
                    answerText[i] = "#"+a.getId()+" < нет текста ответа >";
                }
                answerIds[i] = Integer.toString(a.getId());
                if (correctAnswer.getAnswerId() != null && a.getId() == correctAnswer.getAnswerId().intValue()) {
                    selectedAnswer = Integer.toString(a.getId());
                }
            }
            writer.select(1, "answer_id", answerIds, answerText, selectedAnswer);
        } else {
            writer.writeln("< Вопрос не содержит ответов >");
        }
        writer
                .writeln("Правильльный ответ: Целое число")
                .input(null, "int_result", correctAnswer.getIntResult() == null ? "" : correctAnswer.getIntResult().toString(),null, null)
                .writeln("Правильльный ответ: Строка")
                .input(null, "str_result", correctAnswer.getStrResult() == null ? "" : correctAnswer.getStrResult(),null, null)
                .writeln("Правильльный ответ: Дробное число")
                .input(null, "float_result", correctAnswer.getFloatResult() == null ? "" : correctAnswer.getFloatResult().toString(),null, null)
                .refline("Назад", "./edit_questions?action=edit&question=" + question.getId())
                .submit("Сохранить");
        writer.endForm();
        endPageAndSend(writer, response);
    }

    private void displayCorrectAnswerToDelete(HttpServletResponse response, int questionId, CorrectAnswer answerToDelete) throws IOException {
        HTMLWriter writer = startPage();
        writer
                .title("Редактор вопросов")
                .subtitle("Вы действительно хотите удалить Правильный ответ?")
                .appendHtml("<div>")
                .writeln("Ответ Id : " + answerToDelete.getId());
        if (answerToDelete.getAnswerId() != null) {
            writer.writeln("Id Ответа: " + answerToDelete.getAnswerId());
            Answer answer = questionDAO.getAnswer(answerToDelete.getAnswerId());
            if (answer != null) {
                TextData textData = textDAO.get(answer.getTextDataId());
                if (textData != null) {
                    printTextData(textData, writer);
                } else {
                    writer.writeln("< Текст Вопроса не задан >");
                }
            } else {
                writer.writeln("< нет связанного ответа >");
            }
        } else {
            writer.writeln("< нет связанного ответа >");
        }
        if (answerToDelete.getStrResult() != null) {
            writer.appendHtml("Строка: " + answerToDelete.getStrResult());
        }
        if (answerToDelete.getIntResult() != null) {
            writer.appendHtml("Число: " + answerToDelete.getIntResult());
        }
        if (answerToDelete.getFloatResult() != null) {
            writer.appendHtml("Дробь: " + answerToDelete.getFloatResult());
        }
        writer.appendHtml("</div>")
                .startForm("./edit_questions?action=delete_correct_answer&question="+questionId+"&correct_answer="+answerToDelete.getId(), "POST")
                .submit("Удалить")
                .endForm()
                .refline("Назад", "./edit_questions?action=edit&question="+questionId);
        endPageAndSend(writer, response);
    }

    @Override
    protected Object executeAction(String action, HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        switch(action) {
            case "edit":{
                editQuestion(request, response);
            }
            break;
            case "delete" : {
               deleteQuestion(request, response);
            }
            break;
            case "search":{
//                editQuestion subSectionToEdit = getSubSectionFromRequest(request);
//                editSubSection(request, response, subSectionToEdit, false);
            }
            break;
            case "edit_text_for_question" : {
                editQuestionText(request, response);
            }
            break;
            case "edit_answer" : {
                editQuestionAnswer(request, response);
            }
            break;
            case "delete_answer" : {
                deleteAnswer(request, response);
            }
            break;
            case "edit_correct_answer" : {
                editCorrectAnswer(request, response);
            }
            break;
            case "delete_correct_answer" : {
                deleteCorrectAnswer(request, response);
            }
            break;
        }
        return null;
    }


    private void editQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Question question = getQuestionFromRequest(request);
        if(question == null) {
            showError(request, response, "Идентификатор вопроса не задан или вопрос не найден", "./edit_questions");
        } else {
            Integer type = getParameter("type", request);
            Float rate = getFloatParameter("rate", request);

            if (type == null) {
                displayEditQuestion(request, response, question, "Тип вопроса не задан", "red");
            } else {
                question.setType(type);
                question.setRate(rate);
                questionDAO.updateQuestion(question);
                showMessage(response, "Успешно", "green", "Параметры вопроса сохранены", "./edit_questions?action=edit&question=" + question.getId());
            }
        }
    }


    private void deleteQuestion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Question question = getQuestionFromRequest(request);
        if (question != null) {
            questionDAO.deleteQuestionWithAnswers(question);
            showMessage(response, "Удаление", "blue", "Вопрос \"" +question.getId() + "\" Удален успешно", "./edit_questions?action=list&sub_section="+question.getSubSectionId()+"&section="+question.getSectionId());
        } else {
            showError(request, response, "Не удалось найти Вопрос", "./edit_questions");
        }
    }

    private void editQuestionText(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TextData textData = getTextDataFromRequest("text_id", request);
        Question question = getQuestionFromRequest(request);
        if(question == null) {
            showError(request, response, "Идентификатор вопроса не задан или вопрос не найден", "./edit_questions");
        } else if (textData != null) {

            Integer type = getParameter("type", request);
            String text = request.getParameter("text");
            if (text == null) text = "";
            textData.setText(text);
            if (type == null) {
                displayEditQuestionText(question, textData, response, "Тип текста не задан", "red");
            } else {
                textData.setType(type.intValue());
                textData.setText(text);
                textDAO.update(textData);
                showMessage(response, "Успешно", "green", "Текст вопроса сохранен", "./edit_questions?action=edit&question=" + question.getId());
            }
        } else {
            showError(request, response, "Ошибка текст вопроса не найден или не задан.", "./edit_questions?action=edit&question=" + question.getId());
        }
    }

    private void editQuestionAnswer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TextData textData = getTextDataFromRequest("text_id", request);
        Question question = getQuestionFromRequest(request);
        Answer answer = getAnswerFromRequest(request);
        if(question == null || answer == null || textData == null) {
            showError(request, response, "Идентификатор вопроса и/или Ответа и/или Текста не задан или не найден", "./edit_questions");
        } else {
            Integer type = getParameter("type", request);
            String text = request.getParameter("text");
            Integer position = getParameter("position", request);
            if (text == null) text = "";
            textData.setText(text);
            if (type == null) {
                displayEditAnswer(answer, question, textData, response, "Тип текста не задан", "red");
            } else if (position == null) {
                displayEditAnswer(answer, question, textData, response, "Порядковый номер ответа не задан", "red");
            } else {
                textData.setType(type.intValue());
                //textData.setText(text);
                textDAO.update(textData);
                answer.setPosition(position.intValue());
                questionDAO.updateAnswer(answer);
                showMessage(response, "Успешно", "green", "Ответ сохранен", "./edit_questions?action=edit&question=" + question.getId());
            }
        }
    }

    private void deleteAnswer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Answer answer = getAnswerFromRequest(request);
        Question question = getQuestionFromRequest(request);
        if (answer == null || question == null) {
            showError(request, response, "Идентификатор вопроса и/или Ответа не задан или не найден", "./edit_questions");
        } else {
            questionDAO.deleteAnswerWithTextAndCorrectAnswers(answer);
            showMessage(response, "Удаление", "blue", "Ответ\"" +answer.getId() + "\" Удален успешно", "./edit_questions?action=edit&question=" + question.getId());
        }
    }

    private void editCorrectAnswer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Question question = getQuestionFromRequest(request);
        CorrectAnswer correctAnswer = getCorrectAnswerFromRequest(request);
        if(question == null || correctAnswer == null) {
            showError(request, response, "Идентификатор вопроса и/или Ответа не задан или не найден", "./edit_questions");
        } else {
            Integer answer = getParameter("answer_id", request);
            correctAnswer.setAnswerId(answer);

            Integer intResult = getParameter("int_result", request);
            correctAnswer.setIntResult(intResult);

            String strResult = request.getParameter("str_result");
            if (StringUtils.isEmpty(strResult)) strResult = null;
            correctAnswer.setStrResult(strResult);

            Float floatResult = getFloatParameter("float_result", request);
            correctAnswer.setFloatResult(floatResult);

            questionDAO.updateCorrectAnswer(correctAnswer);
            showMessage(response, "Успешно", "green", "Ответ сохранен", "./edit_questions?action=edit&question=" + question.getId());
        }
    }

    private void deleteCorrectAnswer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CorrectAnswer correctAnswer = getCorrectAnswerFromRequest(request);
        Question question = getQuestionFromRequest(request);
        if (correctAnswer == null || question == null) {
            showError(request, response, "Идентификатор вопроса и/или Правильного ответа не задан или не найден", "./edit_questions");
        } else {
            questionDAO.deleteCorrectAnswer(correctAnswer);
            showMessage(response, "Удаление", "blue", "Ответ\"" +correctAnswer.getId() + "\" Удален успешно", "./edit_questions?action=edit&question=" + question.getId());
        }
    }

    private Question getQuestionFromRequest(HttpServletRequest request) {
        Integer id;
        try {
            id = Integer.parseInt(request.getParameter("question"));
        } catch (Exception ex) {
            ex.printStackTrace();
            id = null;
        }
        Question result = id != null ? questionDAO.getQuestion(id) : null;
        return result;
    }

    private Answer getAnswerFromRequest(HttpServletRequest request) {
        Integer id;
        try {
            id = Integer.parseInt(request.getParameter("answer"));
        } catch (Exception ex) {
            ex.printStackTrace();
            id = null;
        }
        Answer result = id != null ? questionDAO.getAnswer(id) : null;
        return result;
    }

    private CorrectAnswer getCorrectAnswerFromRequest(HttpServletRequest request) {
        Integer id;
        try {
            id = Integer.parseInt(request.getParameter("correct_answer"));
        } catch (Exception ex) {
            ex.printStackTrace();
            id = null;
        }
        CorrectAnswer result = id != null ? questionDAO.getCorrectAnswer(id) : null;
        return result;
    }

    private TextData getTextDataFromRequest(String parameter, HttpServletRequest request) {
        Integer id;
        try {
            id = Integer.parseInt(request.getParameter(parameter));
        } catch (Exception ex) {
            ex.printStackTrace();
            id = null;
        }
        TextData result = id != null ? textDAO.get(id) : null;
        return result;
    }


    private Section getSectionFromRequest(HttpServletRequest request) {
        Integer id;
        try {
            id = Integer.parseInt(request.getParameter("section"));
        } catch (Exception ex) {
            ex.printStackTrace();;
            id = null;
        }
        Section result = id != null ? questionDAO.getSection(id) : null;
        return result;
    }

    private SubSection getSubSectionFromRequest(HttpServletRequest request) {
        Integer id;
        try {
            id = Integer.parseInt(request.getParameter("sub_section"));
        } catch (Exception ex) {
            ex.printStackTrace();;
            id = null;
        }
        SubSection result = id != null ? questionDAO.getSubSection(id) : null;
        return result;
    }

    private int createNewQuestion(Section section, SubSection subSection) {
        TextData textData = textDAO.create(Const.TextDataTypes.SIMPLE_TEXT, "Новый вопрос");
        Question question = new Question();
        question.setRate(1.0f);
        question.setType(Const.QuestionTypes.ONE_CORRECT);
        question.setSectionId(section.getId());
        question.setSubSectionId(subSection.getId());
        question.setTextDataId(textData.getId());
        questionDAO.addQuestion(question);
        return question.getId();
    }

    private int createNewAnswer(Question question) {
        TextData textData = textDAO.create(Const.TextDataTypes.SIMPLE_TEXT, "Новый Ответ");
        Answer answer = new Answer();
        answer.setQuestionId(question.getId());
        answer.setPosition(0);
        answer.setTextDataId(textData.getId());
        questionDAO.addAnswer(answer);
        return answer.getId();
    }

    private int createNewCorrectAnswer(Question question) {
        CorrectAnswer correctAnswer = new CorrectAnswer();
        correctAnswer.setAnswerId(null);
        correctAnswer.setQuestionId(question.getId());
        questionDAO.addCorrectAnswer(correctAnswer);
        return correctAnswer.getId();
    }

    public static String getQuestionTypeShort(int type) {
        switch (type) {
            case Const.QuestionTypes.ONE_CORRECT:
                return "Один верный";
            case Const.QuestionTypes.MANY_CORRECT:
                return "Несколько верных";
            case Const.QuestionTypes.INT_INPUT:
                return "Число";
            case Const.QuestionTypes.FLOAT_INPUT:
                return "Дробь";
            case Const.QuestionTypes.TEXT_INPUT:
                return "Строка";
        }
        return "неизвестно";
    }
*/
}
