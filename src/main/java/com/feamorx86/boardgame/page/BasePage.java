package com.feamorx86.boardgame.page;

import com.fasterxml.jackson.databind.JsonNode;
import com.feamorx86.boardgame.Const;
import com.feamorx86.boardgame.controller.Templates;
import com.feamorx86.boardgame.dao.GameUserDAO;
import com.feamorx86.boardgame.dao.TextDAO;
import com.feamorx86.boardgame.dao.UserDAO;
import com.feamorx86.boardgame.model.GameUser;
import com.feamorx86.boardgame.model.TextData;
import com.feamorx86.boardgame.model.UserModel;
import com.feamorx86.boardgame.utils.HTMLWriter;
import com.feamorx86.boardgame.utils.SimpleTagProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Home on 25.08.2017.
 */
@Controller
public class BasePage  extends AbstractController {

    @Autowired
    protected GameUserDAO gameUserDAO;

    @Autowired
    protected UserDAO userDAO;

    @Autowired
    protected TextDAO textDAO;

    @Autowired
    protected Templates templates;

    @Autowired
    protected RequestManager requestManager;

    public String getName() {
        return getClass().getSimpleName();
    }

    public String getFullName() {
        return getName();
    }

    public String getContentType() {
        return "text/html;charset=cp1251";
    }

    protected Object displayPage(HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException {
        return null;
    }

    protected Object displayAction(String action, HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException {
        return null;
    }

    protected Object executeAction(String action, HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException {
        return null;
    }

    public static boolean isGet(HttpServletRequest request) {
        return "GET".equalsIgnoreCase(request.getMethod());
    }

    public static boolean isPost(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod());
    }

    public Object render(HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException, NoSuchMethodException {
        if (user!=null) {
            String action = request.getParameter("action");
            if (StringUtils.isEmpty(action)) {
                return displayPage(request, response, user);
            } else {
                if (isGet(request)) {
                    return displayAction(action, request, response, user);
                } else if (isPost(request)) {
                    return executeAction(action, request, response, user);
                } else {
                    return null;
                }
            }
        }  else {
            return displayNoUser(request,response);
        }
    }

    protected Object displayNoUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return sendRedirect("./login");
    }

    public Object sendRedirect(String location) {
        return new ModelAndView("redirect:/"+requestManager.getServerLocationPrefix()+location);
    }

    public static Integer getParameter(String name, HttpServletRequest request) {
        Integer result;
        try {
            String param = request.getParameter(name);
            if (param != null) {
                result = Integer.parseInt(param);
            } else {
                result = null;
            }
        } catch (NumberFormatException ex) {
            result = null;
        }
        return result;
    }

    public static Float getFloatParameter(String name, HttpServletRequest request) {
        Float result;
        try {
            String param = request.getParameter(name);
            if (param != null) {
                result = Float.parseFloat(param);
            } else {
                result = null;
            }
        } catch (NumberFormatException ex) {
            result = null;
        }
        return result;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession httpSession = request.getSession(false);

        GameUser user = null;
        if (httpSession != null) {
            Integer userId = (Integer) httpSession.getAttribute("user_id");
            if (userId != null) {
                user = gameUserDAO.get(userId);
            }
        }
        response.setCharacterEncoding("cp1251");
        response.setContentType(getContentType());

        try {
            return (ModelAndView) render(request, response, user);
        } catch (IOException ex) {
            logger.error("Handler request error", ex);
        }
        return null;
    }

    protected HTMLWriter startPage() {
        HTMLWriter writer = new HTMLWriter();
        //writer.startPage();
        writer.appendHtml("<html><head><meta charset=\"utf-8\"/>\n" +
                "<link href='http://fonts.googleapis.com/css?family=Roboto:400,300,100,500' rel='stylesheet' type='text/css'>\n" +
                "<link href='http://fonts.googleapis.com/css?family=Roboto+Slab:400,300,100,500' rel='stylesheet' type='text/css'>\n" +
                "<link rel=\"stylesheet\" href=\"./web/css/style.css\">\n" +
                "</head>\n" +
                "<body>");
        return writer;
    }

    protected void endPageAndSend(HTMLWriter writer, HttpServletResponse response) throws IOException {
        writer.endPage();
        response.setCharacterEncoding("cp1251");
        response.setContentType("text/html;charset=cp1251");
        response.getOutputStream().print(writer.toString());
    }

//    protected void showMessage(HttpServletResponse response, String title, String color, String message, String backLink) throws IOException {
//        HTMLWriter writer = startPage();
//        writer.title(title)
//                .writeln(message, "style=\"color:"+color+"; font-size:14;\"");
//        if (!StringUtils.isEmpty(backLink)) {
//            writer.refline(textDAO.getString("go_back"), backLink);
//        }
//        endPageAndSend(writer, response);
//    }

//    protected void showError(HttpServletRequest request, HttpServletResponse response, String message, String backLink) throws IOException {
//        showMessage(response, textDAO.getString("error_title"), "red", message, backLink);
//    }

    public static void printTextData(TextData text, HTMLWriter writer) {
        if (text != null) {
            printTextData(text.getType(), text.getText(), writer);
        } else {
            writer.writeln("");
        }
    }

    public static void printTextData(int textType, String text, HTMLWriter writer) {
        switch (textType) {
            case Const.TextDataTypes.SIMPLE_TEXT:
                if (!StringUtils.isEmpty(text)) {
                    writer.writeMultiline(text);
                }
                break;
            case Const.TextDataTypes.WEB_VIEW:
                writer.push("div");
                if (!StringUtils.isEmpty(text)) {
                    writer.appendHtml(text);
                }
                writer.pop();
                break;
        }
    }

    public void drawTemplate(SimpleTagProcessor template, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("cp1251");
        response.setContentType("text/html;charset=cp1251");
        ServletOutputStream stream = response.getOutputStream();
        if (stream != null && template != null) {
            for(String block : template.getParsedText()) {
                stream.print(block);
            }
        }
    }

//    protected void displayNav(GameUser user, SimpleTagProcessor page) {
//        SimpleTagProcessor menuItem = templates.getTemplate("nav-menu-item");
//        String userTitle = "";
//        StringBuilder nav = new StringBuilder();
//        if (user != null) {
//            userTitle = (user.getFirstName() == null ? "" : user.getSecondName()) + " " +
//                    (user.getSecondName() == null ? "" : user.getFirstName()) + " " +
//                    (user.getThirdName() == null ? "" : user.getThirdName()); /* + " из " +
//                    (user.getTag() == null ? "" : user.getTag());*/
//            //textDAO.getString("nav.")
//            nav.append(menuItem.withTag("ref", "./login?action=logout").withTag("title", textDAO.getString("nav.change_user")).toString()).append("\n\r")
//                    .append(menuItem.withTag("ref", "./simple_test").withTag("title", textDAO.getString("nav.testing")).toString()).append("\n\r");
//            if (user.getType() == Const.UserTypes.TEACHER || user.getType() == Const.UserTypes.ADMIN) {
//                nav.append(menuItem.withTag("ref", "./edit_questions").withTag("title", textDAO.getString("nav.edit_questions")).toString()).append("\n\r")
//                        .append(menuItem.withTag("ref", "./edit_category").withTag("title", textDAO.getString("nav.edit_category")).toString()).append("\n\r");
//            }
//            if (user.getType() == Const.UserTypes.ADMIN) {
//                nav.append(menuItem.withTag("ref", "./admin").withTag("title", textDAO.getString("nav.admin")).toString()).append("\n\r");
//            }
//            nav.append(menuItem.withTag("ref", "./help?page=start").withTag("title", textDAO.getString("nav.help")).toString()).append("\n\r");
//
//
//        } else {
//            userTitle = textDAO.getString("nav.welcome");
//            nav.append(menuItem.withTag("ref", "./login").withTag("title", textDAO.getString("nav.login")).toString()).append("\r\n")
//                    .append(menuItem.withTag("ref", "./login?action=new").withTag("title", textDAO.getString("nav.registration")).toString()).append("\r\n")
//                    .append(menuItem.withTag("ref", "./help?page=start").withTag("title", textDAO.getString("nav.help")).toString()).append("\r\n");
//        }
//        page.setTag("user-name-title", userTitle);
//        page.setTag("nav-menu", nav.toString());
//    }
    protected String createMainMenu(boolean hasUser) {
        SimpleTagProcessor menu = templates.getTemplate("game_main_menu");
        SimpleTagProcessor menuItem = templates.getTemplate("game_main_menu_item");
        int menuItemsCount = 4;
        String[] titles = new String[]{"На главную", hasUser? "Профиль" : "Вход", "Игры", "О сервисе"};
        String[] urls = new String[]{"/", hasUser ? "/profile" : "/game_login", "/games", "/about"};
        StringBuilder menuItems = new StringBuilder();
        for(int i=0; i < menuItemsCount; i++) {
            menuItem.withTag("url", urls[i]).withTag("title", titles[i]);
            menuItems.append(menuItem.toString()).append("\n\r");
        }
        menu.setTag("game_menu_items_list", menuItems.toString());
        return menu.toString();
    }

    protected String makeJsonError(int code, String message) {
        return "{ \"result\" : \"error\", \"code\": "+Integer.toString(code)+", \"message\" : \""+message+" \"}";
    }

    protected String makeJsonSuccess() {
        return "{ \"result\" : \"success\" }";
    }

    public static class TempResult {
        public boolean success;
        public JsonNode node;
        public JsonNode rootNode;
        public String result;
    }

    protected TempResult check(TempResult temp, String fieldName, String failMessage) {
        temp.node = temp.rootNode.get(fieldName);
        if (temp.node == null) {
            temp.success = false;
            temp.result = failMessage;
        } else {
            temp.result = temp.node.asText();
            if (StringUtils.isEmpty(temp.result)) {
                temp.success = false;
                temp.result = failMessage;
            } else {
                temp.result = temp.result.trim();
                if (StringUtils.isEmpty(temp.result)) {
                    temp.success = false;
                    temp.result = failMessage;
                } else {
                    temp.success = true;
                }
            }
        }
        return temp;
    }

    protected String extractFromJson(JsonNode root, String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node != null) {
            return node.asText();
        } else {
            return null;
        }
    }
}
