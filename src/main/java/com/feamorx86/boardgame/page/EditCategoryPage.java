package com.feamorx86.boardgame.page;

import com.feamorx86.boardgame.Const;
import com.feamorx86.boardgame.dao.QuestionDAO;
import com.feamorx86.boardgame.model.Section;
import com.feamorx86.boardgame.model.SubSection;
import com.feamorx86.boardgame.model.UserModel;
import com.feamorx86.boardgame.utils.HTMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Home on 30.08.2017.
 */
public class EditCategoryPage extends BasePage {

    @Autowired
    private QuestionDAO questionDAO;

    @Override
    public String getName() {
        return "edit_category";
    }

   /* @Override
    protected boolean checkAccessLevel(UserModel user) {
        return user.getType() == Const.UserTypes.ADMIN || user.getType() == Const.UserTypes.TEACHER;
    }

    @Override
    protected Object displayPage(HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        HTMLWriter writer = startPage();
        writer.title("Редкатор разделов")
                .subtitle("Категории");
        List<Section> sections = questionDAO.sectionsByPosition();
        HashMap<Integer, List<SubSection>> subSectionsBySection = new HashMap<>();
        for(Section s : sections) {
            List<SubSection> subSec = questionDAO.getSubSectionsForSection(s.getId());
            if (subSec == null)
                subSec = new ArrayList<>();
            subSectionsBySection.put(s.getId(), subSec);
        }
        writer.startTable("Раздел", "Подраздел", "Описание", "", "", "");
        for(Section section : sections) {
            writer.tableLine(section.getTitle(), section.getDescription() == null ? "" : section.getDescription() , "", "",
                    HTMLWriter.makeRefTag("изменить", "./edit_category?action=edit_section&section="+section.getId()),
                    HTMLWriter.makeRefTag("удалить", "./edit_category?action=delete_section&section="+section.getId()));
            List<SubSection> subSections = subSectionsBySection.get(section.getId());
            if (subSections != null) {
                for(SubSection subSection : subSections) {
                    writer.tableLine("", subSection.getTitle(), subSection.getSubtitle() == null ? "" : subSection.getSubtitle(),
                            HTMLWriter.makeRefTag("вопросы", "./edit_questions?action=list&sub_section="+subSection.getId()+"&section="+section.getId()),
                            HTMLWriter.makeRefTag("изменить", "./edit_category?action=edit_sub_section&sub_section="+subSection.getId()),
                            HTMLWriter.makeRefTag("удалить", "./edit_category?action=delete_sub_section&sub_section="+subSection.getId()));
                }
            }
            writer.tableLine("", HTMLWriter.makeRefTag("новый подраздел", "./edit_category?action=new_sub_section&section="+section.getId()), "", "", "", "");
        }
        writer
                .tableLine(HTMLWriter.makeRefTag("Новый раздел", "./edit_category?action=new_section"), "", "", "", "", "")
                .endTable()
                .refline("На главную", "./start");



        endPageAndSend(writer, response);
        return null;
    }

    @Override
    protected Object displayAction(String action, HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        switch(action) {
            case "new_section" : {
                Section section = new Section();
                section.setTitle("Новый раздел");
                questionDAO.addSection(section);
                displaySectionToEdit(response, section, null, null);
            }
            break;
            case "edit_section": {
                Section sectionToEdit = getSectionByIdFromRequest(request);
                if (sectionToEdit == null) {
                    showError(request, response, "Идентификатор Раздела не указан либо указан не верно.", "./edit_category");
                } else {
                    displaySectionToEdit(response, sectionToEdit, null, null);
                }
            }
            break;
            case "delete_section" : {
                Section sectionToDelete = getSectionByIdFromRequest(request);
                if (sectionToDelete == null) {
                    showError(request, response, "Идентификатор Раздела не указан либо указан не верно.", "./edit_category");
                } else {
                    displaySectionToDelete(response, sectionToDelete);
                }
            }
            break;
            case "edit_sub_section" : {
                SubSection subSectionToEdit = getSubSectionByIdFromRequest(request);
                if (subSectionToEdit == null) {
                    showError(request, response, "Идентификатор Подраздела не указан либо указан не верно.", "./edit_category");
                } else {
                    displaySubSectionToEdit(response, subSectionToEdit, null, null);
                }
            }
            break;
            case "delete_sub_section" : {
                SubSection subSectionToDelete = getSubSectionByIdFromRequest(request);
                if (subSectionToDelete == null) {
                    showError(request, response, "Идентификатор Подраздела не указан либо указан не верно.", "./edit_category");
                } else {
                    displaySubSectionToDelete(request, response, subSectionToDelete);
                }
            }
            break;
            case "new_sub_section" : {
                Section section = getSectionByIdFromRequest(request);
                if (section == null) {
                    showError(request, response, "Идентификатор Раздела не указан либо указан не верно.", "./edit_category");
                } else {
                    SubSection subSection = new SubSection();
                    subSection.setSectionId(section.getId());
                    subSection.setPosition(0);
                    subSection.setTitle("Новый подраздел");
                    questionDAO.addSubSection(subSection);
                    displaySubSectionToEdit(response, subSection, null, null);
                }
            }
                break;
        }
        return null;
    }

    @Override
    protected Object executeAction(String action, HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        switch(action) {
            case "edit_section":{
                editSection(request, response);
            }
            break;
            case "delete_section" : {
                deleteSection(request, response);
            }
            break;
            case "edit_sub_section":{
                editSubSection(request, response);
            }
            break;
            case "delete_sub_section" : {
                deleteSubSection(request, response);
            }
            break;
        }
        return null;
    }

    private void deleteSubSection(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SubSection subSectionToDelete = getSubSectionByIdFromRequest(request);
        if (subSectionToDelete != null) {
            questionDAO.deleteSubSectionWithQuestions(subSectionToDelete);
            showMessage(response, "Удаление", "blue", "Подраздел \"" +subSectionToDelete.getTitle() + "\" Удален успешно", "./edit_category");
        } else {
            showError(request, response, "Не удалось найти Подраздел", "./edit_category");
        }
    }

    private void editSubSection(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SubSection subSection = getSubSectionByIdFromRequest(request);
        subSection.setTitle(request.getParameter("title"));
        subSection.setSubtitle(request.getParameter("subtitle"));
        subSection.setComment(request.getParameter("comment"));
        Integer position = getParameter("position", request);
        if (position == null) {
            subSection.setPosition(0);
        } else {
            subSection.setPosition(position.intValue());
        }

        Integer sectionId = getParameter("section", request);
        if (sectionId == null || !questionDAO.hasSectionWithId(sectionId)) {
            displaySubSectionToEdit(response, subSection, "Идентификатор Раздела не указан либо указан не верно.", "red");
        } else {
            subSection.setSectionId(sectionId.intValue());
            questionDAO.updateSubSection(subSection);
            showMessage(response, "Редактирование подраздела", "green", "Подраздел \"" +subSection.getTitle() + "\" успешно сохранен", "./edit_category");
        }
    }

    private void deleteSection(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Section sectionToDelete = getSectionByIdFromRequest(request);
        if (sectionToDelete != null) {
            List<SubSection> subSectionsToDelete = questionDAO.getSubSectionsForSection(sectionToDelete.getId());
            for(SubSection s : subSectionsToDelete) {
                questionDAO.deleteSubSectionWithQuestions(s);
            }
            questionDAO.deleteSection(sectionToDelete);
            showMessage(response, "Удаление", "blue", "Подраздел \"" +sectionToDelete.getTitle() + "\" Удален успешно", "./edit_category");
        } else {
            showError(request, response, "Не удалось найти Подраздел", "./edit_category");
        }
    }

    private void editSection(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Section section = getSectionByIdFromRequest(request);
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String comment = request.getParameter("comment");
        Integer position = getParameter("position", request);
        if (position == null) position = 0;
        section.setTitle(title);
        section.setDescription(description);
        section.setComment(comment);
        section.setPosition(position);

        questionDAO.updateSection(section);

        displaySectionToEdit(response, section, "Данные сохранены", "green");
    }

    private void displaySectionToDelete(HttpServletResponse response, Section sectionToDelete) throws IOException {
        HTMLWriter writer = startPage();
        writer
                .title("Редкатор разделов")
                .ln()
                .tag("h2", "Вы действительно хотите удалить раздел и все связанные с ним подазделы и вопросы?")
                .writeln((sectionToDelete.getTitle() == null ? "" : sectionToDelete.getTitle()))
                .writeln((sectionToDelete.getDescription() == null ? "" : sectionToDelete.getDescription()))
                .writeln((sectionToDelete.getComment() == null ? "" : sectionToDelete.getComment()))
                .startForm("./edit_category?action=delete_section&section="+sectionToDelete.getId(), "POST")
                .submit("Удалить")
                .endForm()
                .refline("Назад", "./edit_category");
        endPageAndSend(writer, response);
    }

    private void displaySubSectionToDelete(HttpServletRequest request, HttpServletResponse response, SubSection subSectionToDelete) throws IOException {
        HTMLWriter writer = startPage();
        writer
                .title("Редкатор разделов")
                .ln()
                .tag("h2", "Вы действительно хотите удалить подраздел и все связанные с ним вопросы?")
                .writeln((subSectionToDelete.getTitle() == null ? "" : subSectionToDelete.getTitle()))
                .writeln((subSectionToDelete.getSubtitle() == null ? "" : subSectionToDelete.getSubtitle()))
                .writeln((subSectionToDelete.getComment() == null ? "" : subSectionToDelete.getComment()))
                .startForm("./edit_category?action=delete_sub_section&sub_section="+subSectionToDelete.getId(), "POST")
                .submit("Удалить")
                .endForm()
                .refline("Назад", "./edit_category");
        endPageAndSend(writer, response);
    }


    private void displaySectionToEdit(HttpServletResponse response, Section sectionToEdit, String message, String color) throws IOException {
        HTMLWriter writer = startPage();
        writer.title("Редкатор разделов")
                .subtitle("Категории");
        writer.subtitle("Редактирование раздела")
                    .startForm("./edit_category?action=edit_section&section="+sectionToEdit.getId(), "POST");
        if (!StringUtils.isEmpty(message)) {
            writer.writeln(message, "style=\"color:"+color+";\"");
        }
        writer
                .writeln("Зашоловок")
                .input(null, "title", sectionToEdit.getTitle(), null, null)
                .writeln("Описание")
                .input(null, "description", sectionToEdit.getDescription(), null, null)
                .writeln("Коментарий")
                .input(null, "comment", sectionToEdit.getComment(), null, null)
                .writeln("Порядкоывй номер")
                .input(null, "position", Integer.toString(sectionToEdit.getPosition()), null, null)
                .submit("Сохранить")
                .endForm()
                .refline("Назад", "./edit_category");
        endPageAndSend(writer, response);
    }

    private void displaySubSectionToEdit(HttpServletResponse response, SubSection subSectionToEdit, String message, String color) throws IOException {
        HTMLWriter writer = startPage();
        writer.title("Редкатор разделов")
                .subtitle("Категории");
        writer.subtitle("Редактирование подраздела")
                .startForm("./edit_category?action=edit_sub_section&sub_section="+subSectionToEdit.getId(), "POST");
        if (!StringUtils.isEmpty(message)) {
            writer.writeln(message, "style=\"color:"+color+";\"");
        }

        List<Section> sectionsByPosition = questionDAO.sectionsByPosition();
        String [] sectionNames = new String[sectionsByPosition.size()];
        String [] sectionIds = new String[sectionsByPosition.size()];
        for(int i=0; i<sectionsByPosition.size(); i++) {
            Section section = sectionsByPosition.get(i);
            sectionNames[i] = section.getTitle();
            sectionIds[i] = Integer.toString(section.getId());
        }
        String sectionId;
        if (subSectionToEdit != null) {
            sectionId = Integer.toString(subSectionToEdit.getSectionId());
        } else {
            if (sectionIds.length > 0) {
                sectionId = sectionIds[0];
            } else {
                sectionId = "";
            }
        }
        writer
                .writeln("Зашоловок")
                .input(null, "title", subSectionToEdit.getTitle(), null, null)
                .writeln("Подзаголовок")
                .input(null, "subtitle", subSectionToEdit.getSubtitle(), null, null)
                .writeln("Коментарий")
                .input(null, "comment", subSectionToEdit.getComment(), null, null)
                .writeln("Порядкоывй номер")
                .input(null, "position", Integer.toString(subSectionToEdit.getPosition()), null, null)
                .writeln("Раздел")
                .select(sectionsByPosition.size(), "section", sectionIds, sectionNames, sectionId)
                .submit("Сохранить")
                .endForm()
                .refline("Назад", "./edit_category");
        endPageAndSend(writer, response);
    }

    private Section getSectionByIdFromRequest(HttpServletRequest request) {
        Integer id;
        try {
            id = Integer.parseInt(request.getParameter("section"));
        } catch (Exception ex) {
            ex.printStackTrace();
            id = null;
        }
        Section result = id != null ? questionDAO.getSection(id) : null;
        return result;
    }

    private SubSection getSubSectionByIdFromRequest(HttpServletRequest request) {
        Integer id;
        try {
            id = Integer.parseInt(request.getParameter("sub_section"));
        } catch (Exception ex) {
            ex.printStackTrace();
            id = null;
        }
        SubSection result = id != null ? questionDAO.getSubSection(id) : null;
        return result;
    }*/
}
