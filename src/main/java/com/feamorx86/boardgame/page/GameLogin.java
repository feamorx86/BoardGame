package com.feamorx86.boardgame.page;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feamorx86.boardgame.Const;
import com.feamorx86.boardgame.dao.SQLHelper;
import com.feamorx86.boardgame.model.GameUser;
import com.feamorx86.boardgame.utils.SimpleTagProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by feamor on 09.09.2018.
 */
public class GameLogin extends BasePage {

    @Autowired
    private SQLHelper sqlHelper;

    @Override
    public String getName() {
        return "game_login";
    }

    @Override
    protected Object displayPage(HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException {
        return null;
    }

    @Override
    protected Object displayNoUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        if (isPost(request)) {
            if (StringUtils.isEmpty(action)) {
                showLoginPage(response, null);
            } else if (action.equalsIgnoreCase("login")) {
                login(request, response);
            } else {
                showLoginPage(response, null);
            }
        } else {
            showLoginPage(response, null);
        }
        return null;
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String data = request.getParameter("user");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(data);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
            response.getWriter().print(makeJsonError(1, "json parse error :"+e.getMessage()));
        }
        String result;
        TempResult t = new TempResult();
        t.rootNode = root;
        String email, passwordMd5;
        if (check(t, "email", makeJsonError(2, "No email")).success) {
            email = t.result;
            if (check(t, "password", makeJsonError(3, "No password")).success) {
                passwordMd5 = t.result.toUpperCase();
                GameUser newUser = gameUserDAO.checkUserMd5(email, passwordMd5);
                if (newUser!=null) {
                    HttpSession httpSession = request.getSession();
                    if (httpSession == null || StringUtils.isEmpty(httpSession.getId())) {
                        request.getSession(true);
                    }
                    gameUserDAO.createSession(newUser, httpSession.getId());
                    httpSession.setAttribute("user_id", newUser.getId());
                    result = makeJsonSuccess();
                } else {
                    result = makeJsonError(4, "Incorrect login or password");
                }


            } else {
                result = t.result;
            }
        } else {
            result = t.result;
        }
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().print(result);
    }

    private boolean logout(GameUser user, HttpServletRequest request) throws IOException {
        HttpSession httpSession = request.getSession();
        httpSession.removeAttribute("user_id");
        if (httpSession == null || StringUtils.isEmpty(httpSession.getId())) {
            return false;
        } else {
            return gameUserDAO.logout(httpSession.getId());

        }
    }

    @Override
    protected Object displayAction(String action, HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException {
        if (action.equalsIgnoreCase("logout")) {
            logout(user, request);
            response.sendRedirect("/");
        } else {
            showLoginPage(response, user);
        }
        return null;
    }

    @Override
    protected Object executeAction(String action, HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException {
        if (action.equalsIgnoreCase("logout")) {
            if (logout(user, request)) {
                response.getWriter().print(makeJsonSuccess());
            } else {
                response.getWriter().print(makeJsonError(30, "Can't logout, no such session or login"));
            }
        } else {
            response.setContentType("application/json; charset=utf-8");
            response.getWriter().print(makeJsonError(20, "action did not supported"));
        }
        return null;
    }

    private void showLoginPage(HttpServletResponse response, GameUser user) throws IOException {
        SimpleTagProcessor page = templates.getTemplate("game_login");
        page.withTag("main-menu", createMainMenu(user != null))
                .withTag("owner-eamil", Const.AppInfo.OWNER_EMAIL)
                .withTag("app-name", Const.AppInfo.APP_NAME)
                .withTag("user-login-hiden", "")
                .withTag("user-profile", "hidden");
        drawTemplate(page, response);
    }

}
