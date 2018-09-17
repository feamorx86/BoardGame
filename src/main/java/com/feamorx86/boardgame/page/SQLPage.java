package com.feamorx86.boardgame.page;

import com.feamorx86.boardgame.dao.SQLHelper;
import com.feamorx86.boardgame.model.GameUser;
import com.feamorx86.boardgame.utils.HTMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by feamor on 10.09.2018.
 */
public class SQLPage extends BasePage {

    @Autowired
    private SQLHelper sqlHelper;

    @Override
    public String getName() {
        return "sql";
    }

    @Override
    public Object render(HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException, NoSuchMethodException {
        if (isGet(request)) {
            displaySql(request, response, null, null, null);
        } else if (isPost(request)) {
            executeSql(request, response);
        }
        return null;
    }

    private void displaySql(HttpServletRequest request, HttpServletResponse response, String message, String color, List<Object> resultSet) throws IOException {
        HTMLWriter writer = startPage();
        writer
                .title("sql-helper");
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
                .writeln("Warning ", "style=\"color:red;\"fons-size:16")
                .startForm("./sql?action=sql", "POST")
                .writeln("sql:")
                .textArea("code", true, false, 100, 5, null, code)
                .select(1, "type", new String[]{"0", "1"}, new String[]{"result set", "execute update"}, type)
                //.input(null, "admin_password", "", null, null)
                .submit("execute");
        endPageAndSend(writer, response);
    }

    private void executeSql(HttpServletRequest request, HttpServletResponse response)  throws IOException {
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
}
