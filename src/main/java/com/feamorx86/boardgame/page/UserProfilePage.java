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
 * Created by feamor on 10.09.2018.
 */
public class UserProfilePage extends BasePage {

    @Autowired
    private SQLHelper sqlHelper;

    @Override
    public String getName() {
        return "profile";
    }

    @Override
    public Object render(HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException, NoSuchMethodException {
        showLoginPage(response, user);
        return null;
    }

    //    @Override
//    protected Object displayPage(HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException {
//        showLoginPage(response, user);
//        return null;
//    }
//
//    @Override
//    protected Object displayNoUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String action = request.getParameter("action");
//        if (isPost(request)) {
//            if (StringUtils.isEmpty(action)) {
//                showLoginPage(response, null);
//            } else if (action.equalsIgnoreCase("logout")) {
//                logout(request, response);
//            } else {
//                showLoginPage(response, null);
//            }
//        } else {
//            showLoginPage(response, null);
//        }
//        return null;
//    }
//
//    @Override
//    protected Object displayAction(String action, HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException {
//        showLoginPage(response, user);
//        return null;
//    }
//
//    @Override
//    protected Object executeAction(String action, HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException {
//        if (action.equalsIgnoreCase("logout")) {
//            logout(request, response);
//        } else {
//            response.setContentType("application/json; charset=utf-8");
//            response.getWriter().print(makeJsonError(20, "action did not supported"));
//        }
//        return null;
//    }

    private void showLoginPage(HttpServletResponse response, GameUser user) throws IOException {
//        SimpleTagProcessor page = templates.getTemplate("game_login");
//        page.withTag("main-menu", createMainMenu(user != null))
//                .withTag("owner-eamil", Const.AppInfo.OWNER_EMAIL)
//                .withTag("app-name", Const.AppInfo.APP_NAME)
//                .withTag("user-login-hiden", "")
//                .withTag("user-profile", "hidden");
//        drawTemplate(page, response);
        if (user != null) {
            endPageAndSend(
                startPage()
                    .title("Профиль пользователя")
                    .subtitle("Доступные действия")
                    .writeln("Пользователь :"+user.getFirstName() == null ? "": user.getFirstName()+" "+user.getLastName() == null ? "" : user.getLastName())
                    .refline("На главную", "/")
                    .refline("Выход из профиля", "/game_login?action=logout")
                    .endPage(), response);
        } else {
            response.sendRedirect("/");
        }
    }

}
