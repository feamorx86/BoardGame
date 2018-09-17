package com.feamorx86.boardgame.test;

import com.feamorx86.boardgame.Const;
import com.feamorx86.boardgame.dao.QuestionDAO;
import com.feamorx86.boardgame.dao.TextDAO;
import com.feamorx86.boardgame.page.BasePage;
import com.feamorx86.boardgame.utils.HTMLWriter;
import com.feamorx86.boardgame.utils.SimpleTagProcessor;
import com.feamorx86.boardgame.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Home on 01.10.2017.
 */
public class SimpleTestTest extends BasePage {
    @Autowired
    private QuestionDAO questionDAO;
    @Autowired
    private TextDAO textDAO;

    /*private HashMap<Integer, List<QuestionSet>> testsByUserId = new HashMap<>();*/

    //user: list complete tests, list active tests, start test (type, id), finish test, next question, ans to question


    @Override
    public String getName() {
        return "simple_test";
    }

    /*@Override
    protected boolean checkAccessLevel(UserModel user) {
        return user.getType() == Const.UserTypes.STUDENT || user.getType() == Const.UserTypes.TEACHER || user.getType() == Const.UserTypes.ADMIN;
    }

    @Override
    protected Object displayPage(HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        SimpleTagProcessor page = templates.getTemplate("simple_test_base");
        displayNav(user, page);
        drawTemplate(page, response);
        return null;
    }

    @Override
    protected Object displayAction(String action, HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        switch (action) {
            case "list_complete": {
                displayCompleteForUser(request, response, user);
            }
            break;
            case "list_active": {
                displayActiveForUser(request, response, user);
            }
            break;
            case "list_new": {
                displayNewForUser(request, response, user);
            }
            break;
            /////////////
            case "start_test": {
                Integer type = getParameter("type", request); // TODO => getTestFromRequest
                if (type == null) {
                    showError(request, response, "Тип теста не задан", "./simple_test?action=list_new");
                } else {
                    startTest(request, response, user, type);
                }
            }
            break;
            case "show_question" : {
                QuestionSet test = getTestFromRequest(request, response, user, "list_active");
                Integer position = getParameter("position", request);
                if (test != null) {
                    if (position == null || position.intValue() < 0 || position.intValue() >= test.getQuestions().size()) {
                        showError(request, response, "Номер вопроса не задан или вне пределов теста", "./simple_test?action=list_active");
                    } else {
                        test.checkTimeout();
                        if (test.isComplete()) {
                            test.setComplete(true);
                            test.finishTest();
                            test.calculateResult();
                            showError(request, response, "Время вышло. Перейдите к результатам", "./simple_test?action=display_results&test="+test.getId());
                            //response.sendRedirect();
                        } else {
                            showQuestion(request, response, user, test, position);
                        }
                    }
                }
            }
            break;
            case "finish_test" : {
                QuestionSet test = getTestFromRequest(request, response, user, "list_active");
                if (test != null) {
                    displayFinishTest(request, response, user, test);
                }
            }
            break;
            case "display_results" : {
                QuestionSet test = getTestFromRequest(request, response, user, "list_active");
                if (test != null) {
                    test.checkTimeout();
                    if (!test.isComplete()) {
                        return sendRedirect("./simple_test?action=finish_test&test="+test.getId());
                    } else {
                        displayTestResults(request, response, test);
                    }
                }
            }
            break;
            case "view" : {
                QuestionSet test = getTestFromRequest(request, response, user, "list_complete");
                if (test != null) {
                    displayTest(request, response, user, test);
                }
            }
            break;
        }
        return null;
    }

    private void displayTestResults(HttpServletRequest request, HttpServletResponse response,QuestionSet test) throws IOException {
        HTMLWriter writer = startPage();
        writer.title("Тестирование завершено")
                .subtitle(test.getTestName())
                .subtitle("Результаты")
                .line()
                .writeln("Тест пройден за "+test.printTime(test.getFinishTime() - test.getStartTime()))
                .writeln("Результат: "+test.getResult());

        float rate = 0;
        int incorrect = 0;
        HashSet<Integer> sections = new HashSet<>();
        HashSet<Integer> subSections = new HashSet<>();
        for(int i=0; i< test.getQuestions().size(); i++) {
            SelectedQuestion question = test.getQuestions().get(i);
            if (question.checkCorrect()) {
                if (question.question.getRate() == null) {
                    rate += 1.0f;
                } else {
                    rate += question.question.getRate().floatValue();
                }
            } else {
                sections.add(question.getQuestion().getSectionId());
                subSections.add(question.getQuestion().getSubSectionId());
                incorrect++;
            }
        }
        writer.writeln("Итого : "+rate+" баллов.")
                .writeln("Не верных ответов "+incorrect)
                .line();

        if (incorrect > 0) {
            writer.subtitle("Рекомендуем повторить резделы");
            HashMap<Integer, Leaf<Object, Object>> tree = new HashMap<>();
            for (Integer sub : subSections) {
                SubSection subSection = questionDAO.getSubSection(sub);
                if (subSection != null) {
                    Leaf<Object, Object> node = tree.get(subSection.getSectionId());
                    if (node == null) {
                        node = new Leaf<>();
                        node.id = subSection.getSectionId();
                        node.nodes = new ArrayList<Object>();
                        tree.put(subSection.getSectionId(), node);
                    }
                    node.nodes.add(subSection);
                }
            }
            for (Integer sec : sections) {
                if (!tree.containsKey(sec)) {
                    Leaf<Object, Object> node = new Leaf<>();
                    node.id = sec;
                    node.nodes = new ArrayList<Object>();
                    tree.put(sec, node);
                }
            }
            ArrayList<Leaf<Object, Object>> sortedTree = new ArrayList<>();
            for (Leaf<Object, Object> node : tree.values()) {
                Section sec = questionDAO.getSection((Integer) node.id);
                node.id = sec;
                Collections.sort(node.nodes, new Comparator<Object>() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        return ((SubSection) o1).getPosition() - ((SubSection) o2).getPosition();
                    }
                });
                sortedTree.add(node);
            }

            Collections.sort(sortedTree, new Comparator<Leaf<Object, Object>>() {
                @Override
                public int compare(Leaf<Object, Object> o1, Leaf<Object, Object> o2) {
                    return ((Section) o1.id).getPosition() - ((Section) o2.id).getPosition();
                }
            });

            writer.startTable("Раздел", "Подразделы");
            for (Leaf<Object, Object> node : sortedTree) {
                writer.tableLine(((Section) node.id).getTitle(), "");
                for (Object sub : node.nodes) {
                    writer.tableLine("", ((SubSection) sub).getTitle());
                }
            }
            writer.endTable();
        }

        writer.subtitle("Переход")
                .refline("На главную", "./start")
                .refline("К тестам", "./simple_test");
        endPageAndSend(writer, response);
    }


    private void displayFinishTest(HttpServletRequest request, HttpServletResponse response, UserModel user, QuestionSet test) throws IOException {
        HTMLWriter writer = startPage();
        writer.title("Тестирование")
                .subtitle(test.getTestName())
                .writeln("Осталось времени: "+test.printElapsedTime(), "style=\"color:blue;\"")
                .subtitle("Вы действительно хотите завершить тестирование?")
                .line()
                .startForm("./simple_test?action=finish_test&test="+test.getId(), "POST")
                .submit("Завершить")
                .refline("К вопросам", "./simple_test?action=show_question&test="+test.getId()+"&position=0")
                .endForm();
        endPageAndSend(writer, response);
    }

    private void showQuestion(HttpServletRequest request, HttpServletResponse response, UserModel user, QuestionSet test, int position) throws IOException {

        SelectedQuestion question = test.getQuestions().get(position);
        TextData questionText = textDAO.get(question.getQuestion().getTextDataId());

        SimpleTagProcessor page = templates.getTemplate("simple_test_question");
        displayNav(user, page);
        page.setTag("test-title", getNameOfTestByType(test.getType()));
        page.setTag("test-timeout", Long.toString(test.getElapsedTime()));
        page.setTag("question-number", "Вопрос №"+Integer.toString(position+1));


        if (questionText != null) {
            page.setTag("question-text", questionText.getText());
        }

        page.setTag("question-action", "./simple_test?action=show_question&test="+test.getId()+"&question="+question.getQuestion().getId());
        StringBuilder answers = new StringBuilder();
        switch(question.getQuestion().getType()) {
            case Const.QuestionTypes.ONE_CORRECT:
                for (Answer answer : question.getAnswers()) {
                    CorrectAnswer userAnswer = question.findAnswer(answer.getId(), question.getGivenAnswers());
                    TextData answerTextData = textDAO.get(answer.getTextDataId());
                    answers.append("<p><label><input type=\"radio\" name=\"result\" value=\"").append(answer.getId()+"\"")
                            .append(userAnswer != null ? " checked " : "").append("/>").append(answerTextData == null ? "" : answerTextData.getText()).append("</label></p>");
                }
                page.setTag("answers", answers.toString());
            break;
            case Const.QuestionTypes.MANY_CORRECT:
                for (Answer answer : question.getAnswers()) {
                    CorrectAnswer userAnswer = question.findAnswer(answer.getId(), question.getGivenAnswers());
                    TextData answerTextData = textDAO.get(answer.getTextDataId());
                    answers.append("<p><label style=\"margin-left:10px\"><input type=\"checkbox\" name=\"result\" value=\"").append(answer.getId()+"\"")
                            .append(userAnswer != null ? " checked " : "").append("/>").append(answerTextData == null ? "" : answerTextData.getText()).append("</label></p>");
                }
                page.setTag("answers", answers.toString());
                break;
            case Const.QuestionTypes.INT_INPUT:
            case Const.QuestionTypes.FLOAT_INPUT:
            case Const.QuestionTypes.TEXT_INPUT:
                for (Answer answer : question.getAnswers()) {
                    CorrectAnswer userAnswer = question.findAnswer(answer.getId(), question.getGivenAnswers());
                    TextData answerTextData = textDAO.get(answer.getTextDataId());
                    if (answerTextData != null) {
                        answers.append(answerTextData.getText());
                    }
                    String userAnswerText = "";
                    if (userAnswer != null) {
                        if (userAnswer.getIntResult() != null)
                            userAnswerText = Integer.toString(userAnswer.getIntResult());
                        else if (userAnswer.getFloatResult() != null)
                            userAnswerText = Float.toString(userAnswer.getFloatResult());
                        else if (!StringUtils.isEmpty(userAnswer.getStrResult()))
                            userAnswerText = userAnswer.getStrResult();
                    }
                    answers.append("<p><label><input name=\"result_"+answer.getId()+"\" value=\""+userAnswerText+"\" /></label><br><p>");
                }
                page.setTag("answers", answers.toString());
                break;
        }
        SimpleTagProcessor navNextPrev = templates.getTemplate("simple_test_question_next_previous");
        if (position > 0) {
            page.setTag("question-nav-previous", navNextPrev.withTag("title", "Назад").withTag("val", Integer.toString(position - 1)).toString());
        }
        if (position + 1 >= test.getQuestions().size()) {
//            navSend("К завершению", "end", writer);
        } else {
            page.setTag("question-nav-next", navNextPrev.withTag("title", "Далее").withTag("val", Integer.toString(position + 1)).toString());
        }
        StringBuilder builder = new StringBuilder();
        SimpleTagProcessor nav = templates.getTemplate("simple_test_question_nav");
        SimpleTagProcessor navCurrent = templates.getTemplate("simple_test_question_nav_current");
        for(int i = 0; i < test.getQuestions().size(); i++) {
            if (i == position) {
                builder.append(navCurrent.withTag("title", Integer.toString(i + 1)).toString());
            } else {
                builder.append(nav.withTag("val", Integer.toString(i)).withTag("title", Integer.toString(i + 1)).toString());
            }
        }
        page.setTag("questions-nav", builder.toString());
        drawTemplate(page, response);
    }

    private QuestionSet getTestFromRequest(HttpServletRequest request, HttpServletResponse response, UserModel user, String action) throws IOException {
        QuestionSet result = null;
        Integer testId = getParameter("test", request);
        if (testId == null) {
            showError(request, response, "Идентификатор теста не задан ", "./simple_test?action=" + action);
        } else {
            List<QuestionSet> tests = testsByUserId.get(user.getId());
            if (tests == null || tests.size() == 0) {
                showError(request, response, "Тест с таким идентификатором не найден", "./simple_test?action=" + action);
            } else {
                for (QuestionSet q : tests) {
                    if (q.getId() == testId) {
                        result = q;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private AtomicInteger testIdGenerator = new AtomicInteger();

    private static class Leaf<T, I> {
        public I id;
        public List<T> nodes;
    }

    private Leaf<Leaf<Integer, Integer>, Integer> collectSubsectionQuestions(int section, List<Integer> subsection) {
        Leaf<Leaf<Integer, Integer>, Integer> sectionNode = new Leaf<>();
        sectionNode.id = section;
        sectionNode.nodes = new ArrayList<>();

        for(Integer subId : subsection) {
            List<Integer> questions = questionDAO.listQuestionIds(subId);

            Leaf<Integer, Integer> subSectionNode = new Leaf<>();
            subSectionNode.id = subId;
            subSectionNode.nodes = questions;

            sectionNode.nodes.add(subSectionNode);
        }
        return sectionNode;
    }

    private ArrayList<Leaf<Leaf<Integer, Integer>, Integer>> collectQuestions(int[] sections) {
        ArrayList<Leaf<Leaf<Integer, Integer>, Integer>> tree = new ArrayList<>();
        for(int sec : sections) {
            List<Integer> subsection = questionDAO.listSubSectionIds(sec);
            Leaf<Leaf<Integer, Integer>, Integer> sectionNode = collectSubsectionQuestions(sec, subsection);
            tree.add(sectionNode);
        }
        return tree;
    }

    private void collectRandomQuestions(ArrayList<Leaf<Leaf<Integer, Integer>, Integer>> tree, int total, ArrayList<SelectedQuestion> output) {
        Random r = new Random();
        for (int i = 0; i < total; i++) {
            int sectionPos = r.nextInt(tree.size());
            Leaf<Leaf<Integer, Integer>, Integer> sectionNode = tree.get(sectionPos);
            int subSectionPos = r.nextInt(sectionNode.nodes.size());
            Leaf<Integer, Integer> subSectionNode = sectionNode.nodes.get(subSectionPos);
            int questionPos = r.nextInt(subSectionNode.nodes.size());
            int questionId = subSectionNode.nodes.get(questionPos);
            subSectionNode.nodes.remove(questionPos);
            if (subSectionNode.nodes.size() == 0) {
                sectionNode.nodes.remove(subSectionPos);
                if (sectionNode.nodes.size() == 0 ){
                    tree.remove(sectionPos);
                }
            }

            Question question = questionDAO.getQuestion(questionId);
            List<Answer> answers = questionDAO.listAnswers(questionId);

            ArrayList<Answer> mixedAnswers = new ArrayList<>(answers.size());
            int answersSize = answers.size();
            for(int j = 0; j < answersSize; j++) {
                int pos = r.nextInt(answers.size());
                Answer a = answers.get(pos);
                answers.remove(pos);
                mixedAnswers.add(a);
            }

            List<CorrectAnswer> correctAnswers = questionDAO.listCorrectAnswers(questionId);

            SelectedQuestion selectedQuestion = new SelectedQuestion();
            selectedQuestion.setQuestion(question);
            selectedQuestion.setAnswers(mixedAnswers);
            selectedQuestion.setCorrectAnswers(correctAnswers);
            selectedQuestion.setGivenAnswers(new ArrayList<>());

            output.add(selectedQuestion);
        }
    }

    private QuestionSet createTest(int type, long time, int[] sections, int total, int userId) {
        QuestionSet questionSet = new QuestionSet(testIdGenerator.incrementAndGet(), type);
        questionSet.setTimeout(time);
        questionSet.setTestName(getNameOfTestByType(type));

        ArrayList<Leaf<Leaf<Integer, Integer>, Integer>> tree = collectQuestions(sections);
        collectRandomQuestions(tree, total, questionSet.getQuestions());

        List<QuestionSet> tests = testsByUserId.get(userId);
        if (tests == null) {
            tests = new ArrayList<>();
            testsByUserId.put(userId, tests);
        }
        tests.add(questionSet);

        return questionSet;
    }

    private QuestionSet createTestForSubSection(int type, long time, int section, int[] subSections, int total, int userId) {
        QuestionSet questionSet = new QuestionSet(testIdGenerator.incrementAndGet(), type);
        questionSet.setTimeout(time);
        questionSet.setTestName(getNameOfTestByType(type));

        ArrayList<Integer> subsectionList = new ArrayList<Integer>(subSections.length);
        for(int i=0; i<subSections.length; i++) subsectionList.add(subSections[i]);

        Leaf<Leaf<Integer, Integer>, Integer> node = collectSubsectionQuestions(section, subsectionList);
        ArrayList<Leaf<Leaf<Integer, Integer>, Integer>> tree = new ArrayList<>();
        tree.add(node);
        collectRandomQuestions(tree, total, questionSet.getQuestions());

        List<QuestionSet> tests = testsByUserId.get(userId);
        if (tests == null) {
            tests = new ArrayList<>();
            testsByUserId.put(userId, tests);
        }
        tests.add(questionSet);

        return questionSet;
    }

    private QuestionSet createTestForType(UserModel user, int type) {
        QuestionSet result = null;
        switch(type) {
            case 0: {
                //итоговый тест
                result = createTestForSubSection(type, 6 * 4 * 60 * 1000, 136, new int[]{200, 201}, 8, user.getId());
                //result = createTest(type, 6 * 4 * 60 * 1000, new int[]{136, 137}, 6, user.getId());
            }
            break;
            case 1: {
                result = createTestForSubSection(type, 6 * 4 * 60 * 1000, 136, new int[]{200}, 5, user.getId());
                //тест по 1-му разделу
                //result = createTest(type, 6 * 3 * 60 * 1000, new int[]{136}, 6, user.getId());
            }
            break;
            case 2: {
                result = createTestForSubSection(type, 6 * 4 * 60 * 1000, 136, new int[]{201}, 5, user.getId());
                //тест по 2-му разделу
                //result = createTest(type, 3 * 3 * 60 * 1000, new int[]{137}, 3, user.getId());
            }
            break;
//            case 3: {
//                //тест по 3-му разделу
//                int total = 3;
//                result = createTest(type, 3 * 3 * 60 * 1000, new int[]{202}, 3, user.getId());
//            }
        }
        return result;
    }

    private Object startTest(HttpServletRequest request, HttpServletResponse response, UserModel user, int type) throws IOException {
        List<Integer> availableTests = listAvailableTestTypesForUser(user.getId());
        boolean canStart = false;
        for(Integer availableType : availableTests) {
            if (availableType.intValue() == type) {
                canStart = true;
                break;
            }
        }
        if (!canStart) {
            showError(request, response, "Тест недоступен", "./simple_test");
        } else {
            QuestionSet test = createTestForType(user, type);
            if (test != null) {
                test.startTest();
                return sendRedirect("./simple_test?action=show_question&position=0&test=" + test.getId());
            } else {
                showError(request, response, "Не удалось начать тест", "./simple_test");
            }
        }
        return null;
    }

    private void displayTest(HttpServletRequest request, HttpServletResponse response, UserModel user, QuestionSet test) throws IOException {
        HTMLWriter writer = startPage();
        writer.title("Просмотр теста")
                .subtitle("#"+test.getId()+" "+getNameOfTestByType(test.getType()));
        test.checkTimeout();
        if (!test.isComplete()) {
            writer.writeln("Тест не завершен. Просмотр доступен только для завершенных тестов.")
                    .writeln("Осталось времени: "+test.printElapsedTime())
                    .ref("Продолжить тест", "./simple_test?action=show_question&position=0&test="+test.getId())
                    .ref("Назад", "./simple_test?action=list_complete");
        } else {
            writer.writeln("Тест пройден за "+test.printTime(test.getFinishTime() - test.getStartTime()))
                    .writeln("Результат: "+test.getResult())
                    .subtitle("Вопросы теста: ");
            float rate = 0f;
            int incorrect = 0;
            for(int i=0; i< test.getQuestions().size(); i++) {
                SelectedQuestion question = test.getQuestions().get(i);
                writer.line().writeln("Вопрос №"+Integer.toString(i+1));
                if (question.getQuestion().getRate() != null) {
                    writer.writeln("балл : "+question.getQuestion().getRate());
                } else {
                    writer.writeln("балл : 1");
                }
                TextData text = textDAO.get(question.getQuestion().getTextDataId());
                if (text != null) {
                    printTextData(text, writer);
                    writer.writeln("Ответы: ");
                } else {
                    writer.writeln("");
                }
                switch(question.getQuestion().getType()) {
                    case Const.QuestionTypes.ONE_CORRECT:
                        for (Answer answer : question.getAnswers()) {
                            CorrectAnswer userAnswer = question.findAnswer(answer.getId(), question.getGivenAnswers());
                            TextData answerTextData = textDAO.get(answer.getTextDataId());
                            if (userAnswer != null) {
                                writer.input("radio", "q_"+question.question.getId(), null, "checked disabled", answerTextData == null ? "" : answerTextData.getText());
                            } else {
                                writer.input("radio", "q_"+question.question.getId(), null, "disabled", answerTextData == null ? "" : answerTextData.getText());
                            }
                            writer.ln();
                        }
                        break;
                    case Const.QuestionTypes.MANY_CORRECT:
                        for (Answer answer : question.getAnswers()) {
                            CorrectAnswer userAnswer = question.findAnswer(answer.getId(), question.getGivenAnswers());
                            TextData answerTextData = textDAO.get(answer.getTextDataId());
                            if (userAnswer != null) {
                                writer.input("checkbox", "q_"+question.question.getId(), null, "checked disabled", answerTextData == null ? "" : answerTextData.getText());
                            } else {
                                writer.input("checkbox", "q_"+question.question.getId(), null, "disabled", answerTextData == null ? "" : answerTextData.getText());
                            }
                            writer.ln();
                        }
                        break;
                    case Const.QuestionTypes.INT_INPUT:
                    case Const.QuestionTypes.FLOAT_INPUT:
                    case Const.QuestionTypes.TEXT_INPUT:
                        for (Answer answer : question.getAnswers()) {
                            CorrectAnswer userAnswer = question.findAnswer(answer.getId(), question.getGivenAnswers());
                            TextData answerTextData = textDAO.get(answer.getTextDataId());
                            if (answerTextData != null) {
                                printTextData(answerTextData, writer);
                            } else {
                                writer.writeln("");
                            }
                            if (userAnswer != null) {
                                String userAnswerText;
                                if (userAnswer.getIntResult() != null) userAnswerText = Integer.toString(userAnswer.getIntResult());
                                else if (userAnswer.getFloatResult() != null) userAnswerText = Float.toString(userAnswer.getFloatResult());
                                else if (!StringUtils.isEmpty(userAnswer.getStrResult())) userAnswerText = userAnswer.getStrResult();
                                else userAnswerText = "< нет овтета >";
                                writer.input(null, "q_"+question.question.getId(), userAnswerText, "disabled", null);
                            } else {
                                writer.input(null, "q_"+question.question.getId(), "< нет ответа >", "disabled", null);
                            }
                            writer.ln();
                        }
                        break;
                }
                if (question.checkCorrect()) {
                    writer.writeln("Верно", "style=\"color:green;\"");
                    if (question.question.getRate() == null) {
                        rate += 1.0f;
                    } else {
                        rate += question.question.getRate().floatValue();
                    }
                } else {
                    incorrect++;
                    writer.writeln("Не правильно", "style=\"color:red;\"");
                }
            }
            writer
                    .line()
                    .writeln("Итого : "+rate+" баллов.")
                    .writeln("Не верных ответов: "+incorrect)
                    .refline("К списку", "./simple_test?action=list_complete");

        }
        endPageAndSend(writer, response);
    }

    private String getNameOfTestByType(int type) {
        //TODO add it to db
        switch (type) {
            case 0:
                return "Гидростатика : Итоговый тест";
            case 1:
                return "Тест: Гидростатика, теория.";
            case 2:
                return "Тест: Гидростатика, решение задач.";
//            case 3:
//                return "Тест по разделу: ЭЛЕКТРОМАГНИТНОЕ ПОЛЕ";
            default:
                return "";
        }
    }

    private List<Integer> listAvailableTestTypesForUser(int userId) {
        HashSet<Integer> allTests = new HashSet<>();

        allTests.add(0);
        allTests.add(1);
        allTests.add(2);
//        allTests.add(3);

        List<QuestionSet> tests = testsByUserId.get(userId);
        if (tests != null && tests.size() > 0) {
            for (QuestionSet test : tests) {
                test.checkTimeout();
                if (test.isStarted() && !test.isComplete()) {
                    allTests.remove(test.getType());
                }
            }
        }

        ArrayList<Integer> result = new ArrayList<>();
        result.addAll(allTests);
        return result;
    }

    private void displayNewForUser(HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        SimpleTagProcessor page = templates.getTemplate("simple_test_list");
        displayNav(user, page);

        List<Integer> availableTestTypes = listAvailableTestTypesForUser(user.getId());
        page.setTag("list-title", "Доступные тесты:");
        if (availableTestTypes != null) {
            SimpleTagProcessor listItem = templates.getTemplate("simple_test_list_item");
            StringBuilder listStr = new StringBuilder();
            for (Integer testType : availableTestTypes) {
                listStr.append(listItem.withTag("title", getNameOfTestByType(testType.intValue()))
                        .withTag("ref", "./simple_test?action=start_test&type="+testType.intValue()));
            }
            page.setTag("list-items", listStr.toString());
        } else {
            page.setTag("list-items", templates.getTemplate("simple_test_list_no_item").toString());
        }
        drawTemplate(page, response);
    }

    private void displayActiveForUser(HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        SimpleTagProcessor page = templates.getTemplate("simple_test_list");
        displayNav(user, page);

        List<QuestionSet> tests = testsByUserId.get(user.getId());
        page.setTag("list-title", "Текущие тесты:");
        boolean hasAtLeastOneTest = false;
        SimpleTagProcessor listItem = templates.getTemplate("simple_test_list_item");
        StringBuilder listStr = new StringBuilder();

        if (tests!= null) {
            for (QuestionSet test : tests) {
                test.checkTimeout();
                if (test.isStarted() && !test.isComplete()) {
                    hasAtLeastOneTest = true;

                    listStr.append(listItem.withTag("title", "#"+test.getId() + " " + getNameOfTestByType(test.getType()))
                            .withTag("ref", "./simple_test?action=show_question&position=0&test=" + test.getId())
                            .withTag("description",  "( Осталось : " + test.printElapsedTime())+" )");
                }
            }
        }
        if (!hasAtLeastOneTest) {
            page.setTag("list-items", templates.getTemplate("simple_test_list_no_item").toString());
        } else {
            page.setTag("list-items", listStr.toString());
        }
        drawTemplate(page, response);
    }

    private void displayCompleteForUser(HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        SimpleTagProcessor page = templates.getTemplate("simple_test_list");
        displayNav(user, page);

        List<QuestionSet> tests = testsByUserId.get(user.getId());
        page.setTag("list-title", "Завершенные тесты:");
        boolean hasAtLeastOneTest = false;
        SimpleTagProcessor listItem = templates.getTemplate("simple_test_list_item");
        StringBuilder listStr = new StringBuilder();

        if (tests!= null) {
            for (QuestionSet test : tests) {
                if (test.isComplete()) {
                    hasAtLeastOneTest = true;
                    listStr.append(listItem.withTag("title", "#" + test.getId() + " " + getNameOfTestByType(test.getType()))
                            .withTag("ref", "./simple_test?action=view&test=" + test.getId())
                            .withTag("description",  " (" + test.getResult()+")")   );
                }
            }
        }
        if (!hasAtLeastOneTest) {
            page.setTag("list-items", templates.getTemplate("simple_test_list_no_item").toString());
        } else {
            page.setTag("list-items", listStr.toString());
        }
        drawTemplate(page, response);
    }

    @Override
    protected Object executeAction(String action, HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        switch(action) {
            case "show_question" : {
                QuestionSet test = getTestFromRequest(request, response, user, "list_active");
                if (test != null) {
                    Integer questionId = getParameter("question", request);
                    String navigation = request.getParameter("navigation");
                    if (questionId != null) {
                        SelectedQuestion question = test.findQuestion(questionId.intValue());
                        switch(question.getQuestion().getType()) {
                            case Const.QuestionTypes.ONE_CORRECT: {
                                Integer result = getParameter("result", request);
                                question.getGivenAnswers().clear();
                                if (result!= null) {
                                    CorrectAnswer givenAnswer = new CorrectAnswer();
                                    givenAnswer.setAnswerId(result.intValue());
                                    givenAnswer.setQuestionId(question.getQuestion().getId());
                                    question.getGivenAnswers().add(givenAnswer);
                                }
                            }
                            break;
                            case Const.QuestionTypes.MANY_CORRECT: {
                                question.getGivenAnswers().clear();
                                for(Answer answer : question.getAnswers()) {
                                    Integer result = getParameter("result_"+answer.getId(), request);
                                    if (result != null) {
                                        CorrectAnswer givenAnswer = new CorrectAnswer();
                                        givenAnswer.setAnswerId(answer.getId());
                                        givenAnswer.setQuestionId(question.getQuestion().getId());
                                        question.getGivenAnswers().add(givenAnswer);
                                    }
                                }
                            }
                            break;
                            case Const.QuestionTypes.INT_INPUT: {
                                question.getGivenAnswers().clear();
                                for(Answer answer : question.getAnswers()) {
                                    Integer result = getParameter("result_"+answer.getId(), request);
                                    if (result != null) {
                                        CorrectAnswer givenAnswer = new CorrectAnswer();
                                        givenAnswer.setAnswerId(answer.getId());
                                        givenAnswer.setIntResult(result);
                                        givenAnswer.setQuestionId(question.getQuestion().getId());
                                        question.getGivenAnswers().add(givenAnswer);
                                    }
                                }
                            }
                            break;
                            case Const.QuestionTypes.FLOAT_INPUT: {
                                question.getGivenAnswers().clear();
                                for(Answer answer : question.getAnswers()) {
                                    String str = request.getParameter("result_"+answer.getId());
                                    Float result = null;
                                    if (str != null) {
                                        try {
                                            str = str.replace(',', '.');
                                            result = Float.parseFloat(str);
                                        } catch (NumberFormatException ex) {
                                            result = null;
                                        }
                                    }

                                    if (result != null) {
                                        CorrectAnswer givenAnswer = new CorrectAnswer();
                                        givenAnswer.setAnswerId(answer.getId());
                                        givenAnswer.setFloatResult(result);
                                        givenAnswer.setQuestionId(question.getQuestion().getId());
                                        question.getGivenAnswers().add(givenAnswer);
                                    }
                                }
                            }
                            break;
                            case Const.QuestionTypes.TEXT_INPUT: {
                                question.getGivenAnswers().clear();
                                for(Answer answer : question.getAnswers()) {
                                    String result = request.getParameter("result_"+answer.getId());
                                    if (StringUtils.isEmpty(result)) result = null;
                                    if (result != null) {
                                        CorrectAnswer givenAnswer = new CorrectAnswer();
                                        givenAnswer.setAnswerId(answer.getId());
                                        givenAnswer.setStrResult(result);
                                        givenAnswer.setQuestionId(question.getQuestion().getId());
                                        question.getGivenAnswers().add(givenAnswer);
                                    }
                                }
                            }
                            break;
                        }

                        if ("end".equalsIgnoreCase(navigation)) {
                            return sendRedirect("./simple_test?action=finish_test&test="+test.getId());
                        } else {
                            int position;
                            try {
                                position = Integer.parseInt(navigation);
                            } catch(NumberFormatException ex) {
                                ex.printStackTrace();
                                position = 0;
                            }
                            if (position < 0) position = 0;
                            if (position >= test.getQuestions().size()) {
                                position = test.getQuestions().size()- 1;
                            }
                            return sendRedirect("./simple_test?action=show_question&test="+test.getId()+"&position="+position);
                        }

                    } else {
                        showError(request, response, "Идентификатор вопросва не задан", "./simple_test?action=list_active");
                    }
                }
            }
            break;
            case "finish_test" : {
                QuestionSet test = getTestFromRequest(request, response, user, "list_active");
                if (test != null) {
                    test.checkTimeout();
                    if (!test.isComplete()) {
                        test.finishTest();
                        test.calculateResult();
                    }
                    return sendRedirect("./simple_test?action=display_results&test="+test.getId());
                }
            }
            break;
        }
        return null;
    }

    public static class SelectedQuestion {
        private Question question = null;
        private List<Answer> answers = new ArrayList<>();
        private List<CorrectAnswer> correctAnswers = new ArrayList<>();

        private List<CorrectAnswer> givenAnswers = new ArrayList<>();

        public SelectedQuestion() {

        }

        public Question getQuestion() {
            return question;
        }

        public void setQuestion(Question question) {
            this.question = question;
        }

        public List<Answer> getAnswers() {
            return answers;
        }

        public void setAnswers(List<Answer> answers) {
            this.answers = answers;
        }

        public List<CorrectAnswer> getCorrectAnswers() {
            return correctAnswers;
        }

        public void setCorrectAnswers(List<CorrectAnswer> correctAnswers) {
            this.correctAnswers = correctAnswers;
        }

        public List<CorrectAnswer> getGivenAnswers() {
            return givenAnswers;
        }

        public void setGivenAnswers(List<CorrectAnswer> givenAnswers) {
            this.givenAnswers = givenAnswers;
        }

        private CorrectAnswer findAnswer(int answerId, List<CorrectAnswer> searchIn) {
            for(CorrectAnswer c : searchIn) {
                if (c.getAnswerId() == answerId) {
                    return c;
                }
            }
            return null;
        }

        public boolean checkCorrect() {
            if (correctAnswers.size() == givenAnswers.size()) {
                for(CorrectAnswer correct : correctAnswers) {
                    CorrectAnswer userAnswer = findAnswer(correct.getAnswerId(), givenAnswers);
                    if (userAnswer == null) return false;
                    if (!correct.equals(userAnswer)) return false;
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public static class QuestionSet {
        private int id;
        private ArrayList<SelectedQuestion> questions = new ArrayList<>();
        //private UserModel user;
        private long startTime;
        private long finishTime;
        private long timeout;
        //private long lastCheckTime;

        private int type;
        private String testName;

        private boolean isStarted;
        private boolean isComplete;

        private String result;

        public QuestionSet(int id, int type) {
            this.id = id;
            this.type = type;
        }

        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public int getId() {
            return id;
        }

        public long getFinishTime() {
            return finishTime;
        }

        public void startTest() {
            if (!isStarted && !isComplete) {
                isStarted = true;
                startTime = Calendar.getInstance().getTimeInMillis();
                //lastCheckTime = startTime;
            }
        }

        public void finishTest() {
            if (isStarted && !isComplete) {
                isComplete = true;
                finishTime = Calendar.getInstance().getTimeInMillis();
            }
        }

        public long getStartTime() {
            return startTime;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public long getTimeout() {
            return timeout;
        }

        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }

        public boolean isStarted() {
            return isStarted;
        }

        public boolean isComplete() {
            return isComplete;
        }

        public void setComplete(boolean complete) {
            isComplete = complete;
        }

        public ArrayList<SelectedQuestion> getQuestions() {
            return questions;
        }

        public void setQuestions(ArrayList<SelectedQuestion> questions) {
            this.questions = questions;
        }

        public void checkTimeout() {
            if (!isComplete) {
                long now = Calendar.getInstance().getTimeInMillis();
//                long delta = now - lastCheckTime;
//                if (delta < 0) { //защита от смены времени на компьютере
//                    isComplete = true;
//                }
                long delta = now - startTime;
                if (delta >= timeout) {
                    finishTest();
                    calculateResult();
                }
            }
        }

        public void calculateResult() {
            float rate = 0f;
            int errors = 0;
            for(SelectedQuestion question : questions) {
                if (question.checkCorrect()) {
                    if (question.question.getRate() != null) {
                        rate+= question.question.getRate().floatValue();
                    } else {
                        rate += 1.0f;
                    }
                } else {
                    errors++;
                }
            }
            result = "Баллов: "+rate+", Ошибок : "+errors;
        }

        public long getElapsedTime() {
            long delta = Calendar.getInstance().getTimeInMillis() - startTime;
            return timeout - delta;
        }

        public String printElapsedTime() {
            long delta = Calendar.getInstance().getTimeInMillis() - startTime;
            String time = printTime(timeout - delta);
            return time;
        }

        public String printTime(long delta) {
            int hours = (int)(delta / (1000 * 60 * 60));
            delta = delta - hours * 1000 * 60 * 60;
            int minutes = (int)(delta / ( 1000 * 60));
            delta = delta - minutes * 1000 * 60;
            int seconds = (int) (delta / 1000);

            return (hours == 0 ? "" : Integer.toString(hours)+ "ч ") +
                    (minutes == 0 ? "" : Integer.toString(minutes) + "м ") + Integer.toString(seconds)+ "сек";
        }

        public SelectedQuestion findQuestion(int questionId) {
            for(SelectedQuestion i : questions) {
                if (i.getQuestion().getId() == questionId) {
                    return i;
                }
            }
            return null;
        }

//        public UserModel getUser() {
//            return user;
//        }
//
//        public void setUser(UserModel user) {
//            this.user = user;
//        }

    }*/
}
