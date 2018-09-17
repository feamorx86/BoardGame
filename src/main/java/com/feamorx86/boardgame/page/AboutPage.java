package com.feamorx86.boardgame.page;

import com.feamorx86.boardgame.Const;
import com.feamorx86.boardgame.dao.SQLHelper;
import com.feamorx86.boardgame.model.GameUser;
import com.feamorx86.boardgame.model.UserModel;
import com.feamorx86.boardgame.utils.SimpleTagProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by feamor on 09.09.2018.
 */
public class AboutPage extends BasePage {

    @Autowired
    private SQLHelper sqlHelper;

    @Override
    public String getName() {
        return "about";
    }

    @Override
    public Object render(HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException, NoSuchMethodException {
        SimpleTagProcessor processor = templates.getTemplate("about");
        processor
                .withTag("main-menu", createMainMenu(user != null))
                .withTag("version", Const.AppInfo.getVersion())
                .withTag("grs-game", "games/goroda-reki-sela")
                .withTag("home", "/")
                .withTag("owner-email", Const.AppInfo.OWNER_EMAIL)
                .withTag("app-name", Const.AppInfo.APP_NAME);
        drawTemplate(processor, response);
        return null;
    }
}
