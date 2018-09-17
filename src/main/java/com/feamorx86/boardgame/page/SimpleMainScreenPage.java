package com.feamorx86.boardgame.page;

import com.feamorx86.boardgame.Const;
import com.feamorx86.boardgame.dao.SQLHelper;
import com.feamorx86.boardgame.model.GameUser;
import com.feamorx86.boardgame.utils.SimpleTagProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by feamor on 09.09.2018.
 */
public class SimpleMainScreenPage extends BasePage {

    @Autowired
    private SQLHelper sqlHelper;

    @Override
    public String getName() {
        return "index";
    }

    @Override
    protected Object displayPage(HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException {
        drawTemplate(fillUserParameters(user, createPage()
                .withTag("user-login-hiden", "hidden")
                .withTag("user-profile", "")), response);
        return null;
    }

    private SimpleTagProcessor fillUserParameters(GameUser user, SimpleTagProcessor page) {
        page.withTag("user-name", user.getFirstName()+" "+user.getLastName()+ (StringUtils.isEmpty(user.getAlias()) ? "" : " ("+user.getAlias()+")") );
        return page;
    }

    @Override
    protected Object displayNoUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        drawTemplate(createPage()
                .withTag("user-login-hiden", "")
                .withTag("user-profile", "hidden"), response);
        return  null;
    }

    private SimpleTagProcessor createPage() {
        SimpleTagProcessor processor = templates.getTemplate("simple_main_screen");
        processor
                .withTag("about-page-url", "/about")
                .withTag("owner-eamil", Const.AppInfo.OWNER_EMAIL)
                .withTag("app-name", Const.AppInfo.APP_NAME);
        return processor;
    }
}
