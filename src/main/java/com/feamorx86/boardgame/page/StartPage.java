package com.feamorx86.boardgame.page;

import com.feamorx86.boardgame.dao.QuestionDAO;
import com.feamorx86.boardgame.model.GameUser;
import com.feamorx86.boardgame.model.UserModel;
import com.feamorx86.boardgame.utils.SimpleTagProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Home on 25.08.2017.
 */
public class StartPage extends BasePage {

    @Autowired
    private QuestionDAO questionDAO;

    @Override
    public String getName() {
        return "start";
    }
/*
    @Override
    public Object render(HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException, NoSuchMethodException {
        SimpleTagProcessor page = templates.getTemplate("start");
        displayNav(user, page);
        drawTemplate(page, response);
        return null;
    }*/
}
