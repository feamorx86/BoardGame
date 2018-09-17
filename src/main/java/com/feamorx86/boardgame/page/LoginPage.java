package com.feamorx86.boardgame.page;

import com.feamorx86.boardgame.model.UserModel;
import com.feamorx86.boardgame.utils.HTMLWriter;
import com.feamorx86.boardgame.utils.SimpleTagProcessor;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Home on 29.08.2017.
 */
public class LoginPage extends BasePage {
    @Override
    public String getName() {
        return "login";
    }
/*
    @Override
    public Object render(HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException, NoSuchMethodException {
        String action = request.getParameter("action");
        if (user != null) {
            if (StringUtils.isEmpty(action)) {
                return sendRedirect("./start");
            } else {
                return executeAction(action, request, response, user);
            }
        } else {
            if (StringUtils.isEmpty(action)) {
                displayLoginPage(null, null, false, null, null, request, response);
            } else if ("new".equalsIgnoreCase(action) && "GET".equalsIgnoreCase(request.getMethod())) {
                displayRegistrationPage(null, null, null, null, request, response);
            } else if ("login".equalsIgnoreCase(action) && "POST".equalsIgnoreCase(request.getMethod())) {
                String login = request.getParameter("login");
                String password = request.getParameter("password");
                UserModel newUser = userDAO.checkUser(login, password);

                if (newUser!=null) {
//                    String sessionId = request.getRequestedSessionId();
//                    if (StringUtils.isEmpty(sessionId)) {
//                        request.getSession(true);
//                        sessionId = request.changeSessionId();
//                    }
                    HttpSession httpSession = request.getSession();
                    if (httpSession == null || StringUtils.isEmpty(httpSession.getId())) {
                        request.getSession(true);
                    } else {
                        request.changeSessionId();
                    }
                    userDAO.createSession(newUser, httpSession.getId());
                    return sendRedirect("./start");
                } else {
                    displayLoginPage(login, password, true, textDAO.getString("error_title"), textDAO.getString("login.error_incorrect_login_or_password"), request, response);
                }
            } else {
                displayLoginPage(null, null, false, null, null, request, response);
            }
        }
        return null;
    }

    @Override
    protected Object executeAction(String action, HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException {
        switch(action) {
            case "logout":
                userDAO.logout(request.getSession().getId());
                displayLoginPage(null, null, false, textDAO.getString("login.logout_success"), "", request, response);
            break;
            default:
                displayLoginPage(null, null, false, null, null, request, response);
                break;
        }
        return null;
    }

    protected void displayLoginPage(String login, String password, boolean isError, String title, String message, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SimpleTagProcessor page = templates.getTemplate("login");
        SimpleTagProcessor menuItem = templates.getTemplate("nav-menu-item");

        if (!StringUtils.isEmpty(message) || !StringUtils.isEmpty(title)) {
            SimpleTagProcessor messageTemplate;
            if (isError) {
                messageTemplate = templates.getTemplate("dialog-error");
            } else {
                messageTemplate = templates.getTemplate("dialog-message");
            }
            messageTemplate.setTag("title", title);
            messageTemplate.setTag("message", message);
            page.setTag("messages", messageTemplate.toString());
        }

        page.setTag("user-name-title", textDAO.getString("login.welcome"));
        page.setTag("login", login == null ? "" : login);
        page.setTag("password", password == null ? "" : password);
//        page.setTag("nav-menu", new StringBuilder().append(menuItem.withTag("ref", "./login?action=new").withTag("title", "Регистрация").toString()).append("\r\n")
//                .append(menuItem.withTag("ref", "./help?page=login").withTag("title", "Помощь").toString()).append("\r\n").toString());
        displayNav(null, page);
        drawTemplate(page, response);
    }

    protected void displayRegistrationPage(String login, String password, String message, String color, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HTMLWriter writer = startPage();
        writer.title(textDAO.getString("login.registration"));
        if (!StringUtils.isEmpty(message)) {
            writer.writeln(message, "style = \"color:"+color+";\"");
        }
        writer
                .tag("p2", textDAO.getString("login.enter_login_and_password"))
                .startForm("./login?action=login", "POST")
                .writeln(textDAO.getString("login.user_name"))
                .input(null, "login", login, null, null)
                .writeln(textDAO.getString("login.password"))
                .input("password", "password", password, null, null)
                .submit(textDAO.getString("login.enter"))
                .ln()
                .refline(textDAO.getString("login.registration"), "./login?action=new")
                .endForm();
        endPageAndSend(writer, response);
    }*/
}
