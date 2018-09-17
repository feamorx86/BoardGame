package com.feamorx86.boardgame.page;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feamorx86.boardgame.Const;
import com.feamorx86.boardgame.dao.QuestionDAO;
import com.feamorx86.boardgame.model.*;
import com.feamorx86.boardgame.utils.SimpleTagProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by feamor on 29.01.2018.
 */
public class QuestionEditor extends BasePage {

    @Autowired
    private QuestionDAO questionDAO;

    @Override
    public String getName() {
        return "question_editor";
    }
/*
    @Override
    protected Object displayPage(HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        Integer questionId = getParameter("question", request);
        if (questionId != null) {
            Question question = questionDAO.getQuestion(questionId);
            if (question != null) {
                Section section = questionDAO.getSection(question.getSectionId());
                SubSection subSection = questionDAO.getSubSection(question.getSubSectionId());
                List<Answer> answers = questionDAO.listAnswers(question.getId());
                List<CorrectAnswer> correctAnswers = questionDAO.listCorrectAnswers(question.getId());

                SimpleTagProcessor page = templates.getTemplate("question_editor");
                displayNav(user, page);
                displayQuestion(page, question, answers, correctAnswers, section, subSection);

                drawTemplate(page, response);
            } else {
                showError(request, response, textDAO.getStringAndFormat("question_editor.errors.no_such_question", questionId), "./edit_category");
            }
        } else {
            showError(request, response, textDAO.getString("question_editor.errors.no_id"), "./edit_category");
        }
        return null;
    }

    private void setTagsInTemplates(int count, SimpleTagProcessor [] templates, int index, int totalTags, String [] tags, String [] equalValues, String [] notEqualValues, StringBuilder [] outputs) {
        for (int i = 0; i < count; i++) {
            if (i == index) {
                for(int j=0; j<totalTags; j++) {
                    templates[i].setTag(tags[j], equalValues[j]);
                }
            } else {
                for(int j=0; j<totalTags; j++) {
                    templates[i].setTag(tags[j], notEqualValues[j]);
                }
            }
            outputs[i].append(templates[i].toString());
        }
    }

    private void fillEqualValues(String[] equalsArray, int count, String ...valuesArray) {
        for(int i = 0; i<count; i++) {
            equalsArray[i] = valuesArray[i];
        }
    }

    private CorrectAnswer findCorrectAnswerWithAnswerId(List<CorrectAnswer> correctAnswers, int answerId) {
        for (int i = 0; i < correctAnswers.size(); i++) {
            if (correctAnswers.get(i).getAnswerId() == answerId) {
                return correctAnswers.get(i);
            }
        }
        return null;
    }

    private void displayQuestion(SimpleTagProcessor page, Question question, List<Answer> answers, List<CorrectAnswer> correctAnswers, Section section, SubSection subSection) throws IOException {
        SimpleTagProcessor answerTemplate = templates.getTemplate("question_editor_answer");
        SimpleTagProcessor [] correctAnswerTemplates = new SimpleTagProcessor[]{
                templates.getTemplate("question_editor_correct_answer_single"),
                templates.getTemplate("question_editor_correct_answer_multiple"),
                templates.getTemplate("question_editor_correct_answer_int"),
                templates.getTemplate("question_editor_correct_answer_float"),
                templates.getTemplate("question_editor_correct_answer_str")};

        //section
        if (section == null || subSection == null) {
            page.withTag("section-url","./edit_questions").withTag("section-title", textDAO.getString("question_editor.errors.no_section_title"));
            page.withTag("subsection-url","./edit_questions").withTag("subsection-title", textDAO.getString("question_editor.errors.no_subsection_title"));
        } else {
            page.withTag("section-url","./edit_questions").withTag("section-title", section.getTitle())
                    .withTag("subsection-url","./edit_questions?action=list&sub_section="+subSection.getId()+"&section="+section.getId()).withTag("subsection-title", subSection.getTitle());
        }

        //question text type
        TextData questionText = textDAO.get(question.getTextDataId());
        displayTextData(questionText, "question", page);
        //answers
        StringBuilder answersBuilder = new StringBuilder();
        StringBuilder [] builders = new StringBuilder[]{new StringBuilder(), new StringBuilder(), new StringBuilder(), new StringBuilder(), new StringBuilder()};

        String [] correctAnswerTags = new String[]{"index", "value"}, correctAnswerEqualValues = new String[2], correctAnswerNotEqualValues = new String[2];
        CorrectAnswer search;
        if (answers != null) {
            page.withTag("total-answers", Integer.toString(answers.size()));
            page.withTagsInLoop("answer-type-selected-", 0, 5, question.getType(), "selected", "")
                    .withTagsInLoop("answer-type-hidden-", 0, 5, question.getType(), "", "hidden");
            for (int i = 0; i <answers.size(); i++) {
                Answer answer = answers.get(i);
                TextData answerText = textDAO.get(answer.getTextDataId());
                int index = i+1;
                answerTemplate.withTag("index", Integer.toString(index))
                        .withTag("answer-id", Integer.toString(answer.getId()));
                displayTextData(answerText, "answer", answerTemplate);

                answersBuilder.append(answerTemplate.toString());
                search = findCorrectAnswerWithAnswerId(correctAnswers, answer.getId());
                fillEqualValues(correctAnswerNotEqualValues, 2, Integer.toString(index), "");
                switch (question.getType()) {
                    case Const.QuestionTypes.ONE_CORRECT:
                    case Const.QuestionTypes.MANY_CORRECT:
                        fillEqualValues(correctAnswerEqualValues, 2, Integer.toString(index), search != null? "checked" : "");
                    break;
                    case Const.QuestionTypes.INT_INPUT:
                        fillEqualValues(correctAnswerEqualValues, 2, Integer.toString(index),
                                (search != null && search.getIntResult() != null)? Integer.toString(search.getIntResult()) : "");
                    break;
                    case Const.QuestionTypes.FLOAT_INPUT:
                        fillEqualValues(correctAnswerEqualValues, 2, Integer.toString(index),
                                (search != null && search.getFloatResult() != null)? Float.toString(search.getFloatResult()) : "");
                    break;
                    case Const.QuestionTypes.TEXT_INPUT:
                        fillEqualValues(correctAnswerEqualValues, 2, Integer.toString(index),
                                (search != null && search.getStrResult() != null)? search.getStrResult() : "");
                    break;
                }
                setTagsInTemplates(correctAnswerTemplates.length, correctAnswerTemplates, question.getType(), 2, correctAnswerTags, correctAnswerEqualValues, correctAnswerNotEqualValues, builders);
            }
        } else {
            page.withTag("total-answers", "0");
        }

        page.withTag("answers", answersBuilder.toString())
            .withTag("correct-answers-single", builders[0].toString())
            .withTag("correct-answers-multiple", builders[1].toString())
            .withTag("correct-answers-int", builders[2].toString())
            .withTag("correct-answers-float", builders[3].toString())
            .withTag("correct-answers-str", builders[4].toString());

        page.withTag("rate-value", question.getRate() == null? "" : Float.toString(question.getRate()));
    }

    private void displayTextData(TextData textData, String tagPrefix, SimpleTagProcessor template) {
        switch(textData.getType()) {
            case Const.TextDataTypes.SIMPLE_TEXT:
                template.withTagsInLoop(tagPrefix+"-type-select-", 0, 3, 0, "selected", "").withTagsInLoop(tagPrefix+"-text-hidden-", 0, 3, 0, "", "hidden")
                        .withTag(tagPrefix+"-text-simple", textData.getText())
                        .withTag(tagPrefix+"-text-img", "")
                        .withTag(tagPrefix+"-text-html", "")
                        .withTag(tagPrefix+"-text-html-hidden", "hidden");

                break;
            case Const.TextDataTypes.SIMPLE_IMAGE:
                template.withTagsInLoop(tagPrefix+"-type-select-", 0, 3, 1, "selected", "").withTagsInLoop(tagPrefix+"-text-hidden-", 0, 3, 1, "", "hidden")
                        .withTag(tagPrefix+"-text-simple", "")
                        .withTag(tagPrefix+"-text-img", textData.getText())
                        .withTag(tagPrefix+"-text-html", "")
                        .withTag(tagPrefix+"-text-html-hidden", "hidden");
                break;
            case Const.TextDataTypes.WEB_VIEW:
                template.withTagsInLoop(tagPrefix+"-type-select-", 0, 3, 2, "selected", "").withTagsInLoop(tagPrefix+"-text-hidden-", 0, 3, 2, "", "hidden")
                        .withTag(tagPrefix+"-text-simple", "")
                        .withTag(tagPrefix+"-text-img", "")
                        .withTag(tagPrefix+"-text-html", textData.getText())
                        .withTag(tagPrefix+"-text-html-hidden", StringUtils.isEmpty(textData.getText())? "hidden" : "");
                break;
        }
    }

    @Override
    protected Object displayAction(String action, HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        return displayPage(request, response, user);
    }

    @Override
    protected Object executeAction(String action, HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        switch (action) {
            case "save":
                Integer id = getParameter("question", request);
                String data = request.getParameter("data");
                response.setContentType("application/json; charset=utf-8");
                if (id == null || data==null) {
                    response.getOutputStream().print("{ \"result\" : \"error\", \"code\" : 0, \"message\" : \""+textDAO.getString("question_editor.errors.save_invalid_data")+"\" }");
                } else {
                    Question question = questionDAO.getQuestion(id);
                    if (question == null) {
                        response.getOutputStream().print("{ \"result\" : \"error\", \"code\" : 1, \"message\" : \""+textDAO.getStringAndFormat("question_editor.errors.no_such_question", id)+"\" }");
                    } else {
                        String jsonResult = editQuestion(question, data);
                        response.getOutputStream().print(jsonResult);
                    }
                }
                break;
        }
        return null;
    }

    private String makeJsonError(int code, String message) {
        return "\"{ \"result\" : \"error\", \"code\": "+Integer.toString(code)+", \"message\" : \""+message+" \"}";
    }

    private String editQuestion(Question question, String data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(data);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
            return makeJsonError(10, "json parse error :"+e.getMessage());
        }
        if (root.get("id").asInt(-1) != question.getId()) {
            return makeJsonError(11, "request and body question id not same");
        }
        int questionType = root.get("type").asInt(-1);
        if (questionType < Const.QuestionTypes.ONE_CORRECT  || questionType > Const.QuestionTypes.TEXT_INPUT) {
            return makeJsonError(12, "Question type is incorrect : out of range");
        }

        int textType = root.get("textType").asInt(-1);
        if (textType < Const.TextDataTypes.SIMPLE_TEXT || textType > Const.TextDataTypes.WEB_VIEW) {
            return makeJsonError(12, "Question text type is incorrect : out of range");
        }
        String text = root.get("text").asText("");
        TextData questionText = textDAO.get(question.getTextDataId());

        ArrayList<TextData> answersTextData = new ArrayList<>();
        ArrayList<String> correctAnswersText = new ArrayList<>();

        JsonNode array = root.get("answers");
        JsonNode node;
        StringBuilder error = new StringBuilder();

        for(int i = 0;  i < array.size(); i++) {
            node = array.get(i);
            TextData answer = new TextData();
            answer.setType(node.get("type").asInt(-1));
            if (answer.getType()< Const.TextDataTypes.SIMPLE_TEXT || answer.getType()> Const.TextDataTypes.WEB_VIEW) {
                error.append("Incorrect Answer text data ="+answer.getType()+" at position "+i);
            }
            answer.setText(node.get("text").asText(""));
            answersTextData.add(answer);
        }
        if(error.length() > 0) {
            return makeJsonError(13, "Fail to parse answers. Error : \n" + error.toString());
        }
        array = root.get("correctAnswers");
        for(int i = 0; i < array.size(); i++) {
            correctAnswersText.add(array.get(i).asText());
        }


        if (questionType == Const.QuestionTypes.INT_INPUT || questionType == Const.QuestionTypes.FLOAT_INPUT || questionType == Const.QuestionTypes.TEXT_INPUT)  {
            if (correctAnswersText.size() != answersTextData.size()) {
                return makeJsonError(14, "For int float and string amount of answers should be same as amount of correct answers");
            }
        }
        ArrayList<CorrectAnswer> correctAnswers = new ArrayList<>();
        switch (questionType) {
            case Const.QuestionTypes.ONE_CORRECT:{
                if (correctAnswersText.size() != 1) {
                    error.append("Question type is ONE_CORRECT bug correct answers != 1");
                } else {
                    Integer answerIndex = null;
                    try {
                        answerIndex = Integer.parseInt(correctAnswersText.get(0));
                    } catch (NumberFormatException ex) {
                        answerIndex = null;
                        error.append("Incorrect answer index = <"+correctAnswersText.get(0)+"> ");
                    }
                    if (answerIndex!= null) {
                        CorrectAnswer correctAnswer = new CorrectAnswer();
                        correctAnswer.setQuestionId(question.getId());
                        correctAnswer.setAnswerId(answerIndex);
                        correctAnswers.add(correctAnswer);
                    }
                }
            }
            break;
            case Const.QuestionTypes.MANY_CORRECT:{
                ArrayList<Integer> answerIndexes = new ArrayList<>();
                for(String s : correctAnswersText) {
                    try {
                        answerIndexes.add(Integer.parseInt(s));
                    } catch (NumberFormatException ex) {
                        error.append("Incorrect answer index = <"+s+"> ");
                    }
                }
                for(int i = 0; i < answerIndexes.size(); i++) {
                    CorrectAnswer correctAnswer = new CorrectAnswer();
                    correctAnswer.setQuestionId(question.getId());
                    correctAnswer.setAnswerId(answerIndexes.get(i));
                    correctAnswers.add(correctAnswer);
                }
            }
            break;
            case Const.QuestionTypes.INT_INPUT:{
                ArrayList<Integer> intAnswers = new ArrayList<>();
                for(String s : correctAnswersText) {
                    try {
                        intAnswers.add(Integer.parseInt(s));
                    } catch (NumberFormatException ex) {
                        error.append("Incorrect Int answer = <"+s+"> ");
                    }
                }
                for(int i = 0; i < intAnswers.size(); i++) {
                    CorrectAnswer correctAnswer = new CorrectAnswer();
                    correctAnswer.setQuestionId(question.getId());
                    correctAnswer.setAnswerId(i);
                    correctAnswer.setIntResult(intAnswers.get(i));
                    correctAnswers.add(correctAnswer);
                }
            }
            break;
            case Const.QuestionTypes.FLOAT_INPUT:{
                ArrayList<Float> floatAnswers = new ArrayList<>();
                for(String s : correctAnswersText) {
                    try {
                        floatAnswers.add(Float.parseFloat(s));
                    } catch (NumberFormatException ex) {
                        error.append("Incorrect Float answer = <"+s+"> ");
                    }
                }
                for(int i = 0; i < floatAnswers.size(); i++) {
                    CorrectAnswer correctAnswer = new CorrectAnswer();
                    correctAnswer.setQuestionId(question.getId());
                    correctAnswer.setAnswerId(i);
                    correctAnswer.setFloatResult(floatAnswers.get(i));
                    correctAnswers.add(correctAnswer);
                }
            }
            break;
            case Const.QuestionTypes.TEXT_INPUT:{
                for(int i = 0; i < correctAnswersText.size(); i++) {
                    CorrectAnswer correctAnswer = new CorrectAnswer();
                    correctAnswer.setQuestionId(question.getId());
                    correctAnswer.setAnswerId(i);
                    correctAnswer.setStrResult(correctAnswersText.get(i));
                    correctAnswers.add(correctAnswer);
                }
            }
            break;
        }
        if(error.length() > 0) {
            return makeJsonError(15, "Fail to parse correct answers. Error : \n" + error.toString());
        }
        try {
            //TODO; Lock db and add make it in one transaction
            List<Answer> oldAnswers = questionDAO.listAnswers(question.getId());
            for (Answer answer : oldAnswers) {
                questionDAO.deleteAnswerWithTextAndCorrectAnswers(answer);
            }
            ArrayList<Answer> answers = new ArrayList<>();
            for (int i = 0; i < answersTextData.size(); i++) {
                TextData answerText = answersTextData.get(i);
                textDAO.save(answerText);
                Answer answer = new Answer();
                answer.setTextDataId(answerText.getId());
                answer.setQuestionId(question.getId());
//            answer.setPosition(i);
                questionDAO.addAnswer(answer);
                answers.add(answer);
            }
            for (int i = 0; i < correctAnswers.size(); i++) {
                CorrectAnswer correctAnswer = correctAnswers.get(i);
                correctAnswer.setAnswerId(answers.get(correctAnswer.getAnswerId()).getId()); // correct_answer_index -> answer_id
                questionDAO.addCorrectAnswer(correctAnswer);
            }
            questionText.setType(textType);
            questionText.setText(text);
            textDAO.update(questionText);
            question.setType(questionType);
            questionDAO.updateQuestion(question);
        } catch (Exception ex) {
            ex.printStackTrace();
            return makeJsonError(100, "Db error, fail to save or update question with id = "+question.getId()+", error :" +ex.getMessage());
        }

        return "\"{ \"result\" : \"success\" }";
    }*/
}
