package com.feamorx86.boardgame.page;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feamorx86.boardgame.dao.SQLHelper;
import com.feamorx86.boardgame.model.GameUser;
import com.feamorx86.boardgame.model.UserModel;
import com.feamorx86.boardgame.utils.SimpleTagProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by feamor on 04.09.2018.
 */
public class GameRegistrationPage extends BasePage {

    @Autowired
    private SQLHelper sqlHelper;

    @Override
    public String getName() {
        return "game_registration";
    }

    @Override
    protected Object displayNoUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        if (isPost(request)) {
            if (StringUtils.isEmpty(action)) {
                return showRegistration(request, response, null);
            } else if (action.equalsIgnoreCase("new")) {
                checkAndProcessRegistration(request, response);
                return null;
            } else {
                return showRegistration(request, response, null);
            }
        } else {
            return showRegistration(request, response, null);
        }
    }

    @Override
    protected Object displayPage(HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException {
        return showRegistration(request, response, user);
    }

    @Override
    protected Object displayAction(String action, HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException {
        return showRegistration(request, response, user);
    }

    @Override
    protected Object executeAction(String action, HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException {
        return null;
    }



    private void checkAndProcessRegistration(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

        String email, password, firstName, secondName, alias;

        if (check(t, "email", makeJsonError(2, "No email")).success) {
            if (gameUserDAO.hasUserWithEmail(t.result)) {
                result = makeJsonError(3, "Have user with such email");
            }  else {
                email = t.result;
                if (check(t, "password", makeJsonError(4, "No password")).success) {
                    password = t.result;
                    if (check(t, "first_name", makeJsonError(5, "No first name")).success) {
                        firstName = t.result;

                        secondName = extractFromJson(root, "second_name");
                        alias = extractFromJson(root, "alias");

                        GameUser user = new GameUser();
                        user.setEmail(email);
                        user.setPassword(password);
                        user.setFirstName(firstName);
                        user.setLastName(secondName);
                        user.setAlias(alias);

                        boolean success;
                        try {
                            gameUserDAO.saveUser(user);
                            success = true;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            success = false;
                        }
                        if (success ) {
                            result = "{\"result\" : \"success\", \"user_id\": \"" + Integer.toString(user.getId()) + "\"}";
                        } else {
                            result = makeJsonError(6, "Fail to save user");
                        }

                    } else {
                        result = t.result;
                    }
                } else {
                    result = t.result;
                }
            }
        } else {
            result = t.result;
        }
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().print(result);
    }

    public Object showRegistration(HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException {
        SimpleTagProcessor processor = templates.getTemplate("registration_new");
        processor.withTag("main-menu", createMainMenu(user != null))
                .withTag("login-url", "/game_login");
        drawTemplate(processor, response);
        return null;
    }
}
