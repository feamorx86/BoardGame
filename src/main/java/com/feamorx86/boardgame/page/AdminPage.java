package com.feamorx86.boardgame.page;

import com.feamorx86.boardgame.Const;
import com.feamorx86.boardgame.dao.SQLHelper;
import com.feamorx86.boardgame.model.UserModel;
import com.feamorx86.boardgame.utils.HTMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Home on 28.08.2017.
 */
public class AdminPage extends BasePage {

    @Autowired
    private SQLHelper sqlHelper;

    @Override
    public String getName() {
        return "admin";
    }

 /*   @Override
    protected Object displayPage(HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        defaultPage(request, response, user, "", "");
        return null;
    }

    private void defaultPage(HttpServletRequest request, HttpServletResponse response, UserModel user, String message, String color) throws IOException {
        HTMLWriter writer = startPage();
        writer
                .title(textDAO.getString("admin.title"))
                .ln()
                .writeln(textDAO.getStringAndFormat("admin.welcome", user.getFirstName(), user.getSecondName()));

        if (!StringUtils.isEmpty(message)) {
            writer.writeln(message, "style= \"color:"+color+";\"");
        }

        writer.writeln(textDAO.getString("admin.actions"), "style= \"color:blue;\"")
                .push("div")
                .refline(textDAO.getString("admin.actions.profile"), "./admin?action=edit&user="+user.getId())
                .refline(textDAO.getString("admin.actions.users_list"), "./admin?action=list")
                .refline(textDAO.getString("admin.actions.new_user"), "./admin?action=new")
                .refline(textDAO.getString("admin.actions.search_user"), "./admin?action=search")
                .refline(textDAO.getString("admin.actions.edit_questions"), "./edit_questions")
                .refline(textDAO.getString("admin.actions.settings"), "./admin?action=settings")
                .refline(textDAO.getString("admin.actions.edit_files"), "./admin?action=edit_files")
                .refline(textDAO.getString("admin.actions.reload_strings"), "./admin?action=reload_strings")
                .refline(textDAO.getString("admin.actions.clear_cached_templates"), "./admin?action=clear_cached_templates")
                .refline(textDAO.getString("admin.actions.sql"), "./admin?action=sql")
                .refline(textDAO.getString("admin.to_start"), "./start")
                .pop();
        endPageAndSend(writer, response);
    }

    @Override
    protected Object displayAction(String action, HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        switch(action) {
            case "edit":{
                UserModel userToEdit = getUserFromRequest(request);
                if (userToEdit == null) {
                    showError(request, response, textDAO.getString("admin.error.invalid_user_id"), "./admin");
                } else {
                    displayUserToEdit(response, userToEdit, null, null, false);
                }
            }
            break;
            case "new" :
                displayUserToEdit(response, null, null, null, true);
            break;
            case "delete" : {
                UserModel userToDelete = getUserFromRequest(request);
                if (userToDelete == null) {
                    showError(request, response, textDAO.getString("admin.error.invalid_user_id"), "./admin");
                } else {
                    displayUserToDelete(request, response, userToDelete);
                }
            }
            break;
            case "list":
                listUsers(request, response);
            break;
            case "search": {
                displaySearch(request, response, null, null, null);
                //ArrayList<UserModel> users = findUsers();
            }
            break;
            case "settings":
                //displayAppSettings(request, response);
                defaultPage(request, response, user, textDAO.getString("not_implemented"), "blue");
                break;
            case "sql" :
                displaySql(request, response, null, null, null);
                break;
            case "reload_strings": {
                textDAO.loadStrings();
                defaultPage(request, response, user, textDAO.getString("admin.strings_reloaded"), "green");
            }
            break;
            case "clear_cached_templates":{
                templates.clear();
                defaultPage(request, response, user, textDAO.getString("admin.templates_removed"), "green");
            }
            break;

        }
        return null;
    }

    @Override
    protected Object executeAction(String action, HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        switch(action) {
            case "edit":{
                //UserModel userToEdit = getUserFromRequest(request);
                editUser(request, response, false);
            }
            break;
            case "search": {
                String query = request.getParameter("query");
                if (StringUtils.isEmpty(query)) {
                    displaySearch(request, response, null, textDAO.getString("admin.search.title"), "red");
                } else {
                    List<UserModel> searchResult = userDAO.findByField(query);
                    if (searchResult.size() == 0) {
                        displaySearch(request, response, query, textDAO.getString("admin.search.no_users"), "red");
                    } else {
                        HTMLWriter writer = startPage();
                        writer
                                .title(textDAO.getString("admin.title"))
                                .ln()
                                .tag("h2", textDAO.getString("admin.search.results"))
                                .writeln(textDAO.getStringAndFormat("admin.search.query", query));
                        printUsersList(searchResult, writer);
                        writer.refline(textDAO.getString("admin.back"), "./admin");
                        endPageAndSend(writer, response);
                    }
                }
            }
            break;
            case "settings":
                break;
            case "delete":{
                UserModel userToDelete = getUserFromRequest(request);
                if (userToDelete != null) {
                    userDAO.deleteUser(userToDelete);
                    showMessage(response, textDAO.getString("admin.delete.title"),"blue",
                            textDAO.getStringAndFormat("admin.delete.message", userToDelete.getFirstName(), userToDelete.getSecondName()), "./admin");
                } else {
                    showError(request, response, textDAO.getString("admin.delete.error"), "./admin");
                }
            }
            break;
            case "new" :
                editUser(request, response, true);
            break;
            case "sql" : {
                executeSql(request, response, user);
            }
            break;
        }
        return null;
    }

    private void displayUserToDelete(HttpServletRequest request, HttpServletResponse response, UserModel userToDelete) throws IOException {
        HTMLWriter writer = startPage();
        writer
                .title(textDAO.getString("admin.title"))
                .ln()
                .tag("h2", textDAO.getString("admin.delete.question"))
                .writeln((userToDelete.getFirstName() == null ? "" : userToDelete.getFirstName()) + " " +
                        (userToDelete.getSecondName() == null ? "" : userToDelete.getSecondName()) + " " +
                        (userToDelete.getThirdName() == null ? "" : userToDelete.getThirdName()) + " " +
                        (userToDelete.getTag() == null ? "" : userToDelete.getTag()) )
                .startForm("./admin?action=delete&user="+userToDelete.getId(), "POST")
                .submit(textDAO.getString("admin.delete.title"))
                .endForm()
                .refline(textDAO.getString("admin.delete.to_list"), "./admin?action=list");
        endPageAndSend(writer, response);
    }

    private void displaySql(HttpServletRequest request, HttpServletResponse response, String message, String color, List<Object> resultSet) throws IOException {
        HTMLWriter writer = startPage();
        writer
                .title(textDAO.getString("admin.title"))
                .subtitle(textDAO.getString("admin.sql.title"));
        if (!StringUtils.isEmpty(message)) writer.writeMultiline(message, "style=\"color:"+color+"\";");

        if (resultSet != null) {
            if (resultSet.size() > 0) {
                Object[] row = (Object[]) resultSet.get(0);
                String[] rowTypes = new String[row.length];
                for(int i=0; i < row.length; i++) {
                    if (row[i] == null) {
                        rowTypes[i] = "(null)";
                    } else {
                        rowTypes[i] = row[i].getClass().getSimpleName();
                    }
                }
                writer.startTable(rowTypes);
                for(int i=0; i< resultSet.size(); i++) {
                    row = (Object[]) resultSet.get(i);
                    String[] rowText = new String[row.length];
                    for(int j = 0; j <row.length; j++) {
                        if (row[j] == null) {
                            rowText[j] = "<null>";
                        } else {
                            rowText[j] = row[j].toString();
                        }

                    }
                    writer.tableLine(rowText);
                }
                writer.endTable();
            }
            writer.writeln("Total rows : "+resultSet.size());
        }

        String code = request.getParameter("code");
        if (code == null) code = "";
        String type = request.getParameter("type");
        if (type == null) type = "0";

        writer
                .writeln(textDAO.getString("admin.sql.warning"), "style=\"color:red;\"fons-size:16")
                .startForm("./admin?action=sql", "POST")
                .writeln("sql:")
                .textArea("code", true, false, 100, 5, null, code)
                .select(1, "type", new String[]{"0", "1"}, new String[]{"result set", "execute update"}, type)
                //.input(null, "admin_password", "", null, null)
                .submit("execute");
        writer.refline(textDAO.getString("admin.back"), "./admin");
        endPageAndSend(writer, response);
    }

    private void executeSql(HttpServletRequest request, HttpServletResponse response, UserModel admin)  throws IOException {
//        String adminPassword = request.getParameter("admin_password");
//        if (StringUtils.isEmpty(adminPassword) || !admin.getPassword().equals(adminPassword)) {
//            userDAO.closeAllSessions(admin.getId());
//            displayAccessDenied(response, "Админимтрирование");
//        } else {
            String code = request.getParameter("code");
            Integer type = getParameter("type", request);
            if (type == null) type = 0;

            boolean requestSuccess;
            ArrayList<Object> resultSet;

            if (type == 0) {
                resultSet = new ArrayList<>();
                boolean result = sqlHelper.executeList(code, null, null, resultSet);
                if (result) {
                    displaySql(request, response, textDAO.getString("admin.sql.success"), "green", resultSet);
                } else {
                    displaySql(request, response, textDAO.getStringAndFormat("admin.sql.error", resultSet.get(0).toString()), "red", null);
                }
            } else {
                resultSet = new ArrayList<>();
                boolean result = sqlHelper.executeUpdate(code, null, resultSet);
                if (result) {
                    displaySql(request, response, textDAO.getStringAndFormat("admin.sql.updated", resultSet.get(0).toString()), "green", null);
                } else {
                    displaySql(request, response, textDAO.getStringAndFormat("admin.sql.error", resultSet.get(0).toString()), "red", null);
                }
            }
//        }
    }


    private String userTypeToStr(int type) {
        switch (type) {
            case Const.UserTypes.ADMIN : return textDAO.getString("admin.type.admin");
            case Const.UserTypes.STUDENT: return textDAO.getString("admin.type.student");
            case Const.UserTypes.TEACHER: return textDAO.getString("admin.type.teacher");
            default: return " - ";
        }
    }

    private void printUsersList(List<UserModel> users, HTMLWriter writer) {
        writer.startTable("#", textDAO.getString("admin.word.type"), textDAO.getString("admin.word.first"), textDAO.getString("admin.word.second"),
                textDAO.getString("admin.word.group"), "...", "x");
        for(UserModel user : users) {
            writer.tableLine(Integer.toString(user.getId()), userTypeToStr(user.getType()),
                    user.getFirstName(), user.getSecondName(), user.getTag(),
                    HTMLWriter.makeRefTag(textDAO.getString("admin.word.change"), "./admin?action=edit&user="+user.getId()),
                    HTMLWriter.makeRefTag(textDAO.getString("admin.word.remove"), "./admin?action=delete&user="+user.getId()));
        }
        writer.endTable();
    }

    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HTMLWriter writer = startPage();
        writer
                .title(textDAO.getString("admin.title"))
                .ln()
                .tag("h2", textDAO.getString("admin.users.list"));
        printUsersList(userDAO.getAllUsers(), writer);
        writer.refline(textDAO.getString("admin.users.new"), "./admin?action=new");
        writer.refline(textDAO.getString("admin.search"), "./admin?action=search");
        writer.refline(textDAO.getString("admin.back"), "./admin");
        endPageAndSend(writer, response);
    }

    private void displaySearch(HttpServletRequest request, HttpServletResponse response, String query, String message, String color) throws IOException {
        HTMLWriter writer = startPage();
        writer
                .title(textDAO.getString("admin.title"))
                .ln()
                .tag("h2", textDAO.getString("admin.search"));
        if (!StringUtils.isEmpty(message)) {
            writer.writeln(message, "style=\"color:"+color+";\"");
        }
        writer
                .startForm("./admin?action=search", "POST")
                .input(null, "query", query, null, null)
                .submit(textDAO.getString("admin.search"))
                .endForm()
                .refline(textDAO.getString("admin.search.all"), "./admin?action=list")
                .refline(textDAO.getString("admin.back"), "./admin");
        endPageAndSend(writer, response);
    }


    private UserModel userFromRequest(HttpServletRequest request) {
        UserModel user = new UserModel();
        user.setFirstName(request.getParameter("firstName"));
        user.setSecondName(request.getParameter("secondName"));
        user.setThirdName(request.getParameter("thirdName"));
        user.setType(Integer.parseInt(request.getParameter("userType")));
        user.setTag(request.getParameter("tag"));
        user.setLogin(request.getParameter("login"));
        String password = request.getParameter("password");
        if (!StringUtils.isEmpty(password)) {
            user.setPassword(password);
        }
        return user;
    }


    private void displayUserToEdit(HttpServletResponse response, UserModel userToEdit, String message, String color, boolean isNew) throws IOException {
        HTMLWriter writer = startPage();
        writer
                .title(textDAO.getString("admin.title"))
                .ln();
        if (!isNew) {
            writer.tag("h2", textDAO.getString("admin.edit.title"))
                    .startForm("./admin?action=edit&user="+userToEdit.getId(), "POST");
        } else {
            writer.tag("h2", textDAO.getString("admin.edit.new_title"))
                    .startForm("./admin?action=new", "POST");
        }
        if (!StringUtils.isEmpty(message)) {
            writer.writeln(message, "style=\"color:"+color+";\"");
        }
        String firstName = null, secondName = null, thirdName = null, tag = null, login = null, password = null;
        int type = Const.UserTypes.STUDENT;

        if (userToEdit != null) {
            firstName = userToEdit.getFirstName();
            secondName = userToEdit.getSecondName();
            thirdName = userToEdit.getThirdName();
            tag = userToEdit.getTag();
            login = userToEdit.getLogin();
            password = userToEdit.getPassword();
            type = userToEdit.getType();
        }
        writer
                .writeln(textDAO.getString("admin.word.second"))
                .input(null, "secondName", secondName, null, null)
                .writeln(textDAO.getString("admin.word.first"))
                .input(null, "firstName", firstName, null, null)
                .writeln(textDAO.getString("admin.word.third"))
                .input(null, "thirdName", thirdName, null, null)
                .writeln(textDAO.getString("admin.word.third"))
                .input(null, "tag", tag, null, null)
                .writeln(textDAO.getString("admin.word.login"))
                .input(null, "login", login, null, null)
                .writeln(textDAO.getString("admin.word.password"))
                .input("password", "password", password, null, null)
                .select(3, "userType", new String[]{"0", "1", "2"}, new String[]{textDAO.getString("admin.type.student"), textDAO.getString("admin.type.teacher"), textDAO.getString("admin.type.admin")}, type)
                .submit(textDAO.getString("admin.word.save"))
                .endForm()
                .refline(textDAO.getString("admin.users.to_list"), "./admin?action=list")
                .refline(textDAO.getString("admin.back"), "./admin");
        endPageAndSend(writer, response);
    }


    private boolean editUser(HttpServletRequest request, HttpServletResponse response, boolean isNew) throws IOException {
        UserModel user = userFromRequest(request);
        boolean success;
        if (!isNew) {
            Integer id = getParameter("user", request);
            if (id == null) {
                success = false;
                showError(request, response, textDAO.getString("admin.error.invalid_user_id"), "./admin");
            } else {
                user.setId(id.intValue());
                if (userDAO.updateUser(user)) {
                    displayUserToEdit(response, user, textDAO.getString("admin.edit.success"), "green", isNew);
                    success = true;
                } else {
                    displayUserToEdit(response, user, textDAO.getStringAndFormat("admin.edit.error_exist", user.getLogin()), "red", isNew);
                    success = false;
                }
            }
        } else {
            if (userDAO.add(user)) {
                displayUserToEdit(response, user, textDAO.getString("admin.edit.success"), "green", isNew);
                success = true;
            } else {
                displayUserToEdit(response, user, textDAO.getStringAndFormat("admin.edit.error_exist", user.getLogin()), "red", isNew);
                success = false;
            }
        }
        return success;
    }

    private UserModel getUserFromRequest(HttpServletRequest request) {
        Integer userId;
        try {
            userId = Integer.parseInt(request.getParameter("user"));
        } catch (Exception ex) {
            ex.printStackTrace();;
            userId = null;
        }
        UserModel result = userId != null ? userDAO.get(userId) : null;
        return result;
    }*/

}
