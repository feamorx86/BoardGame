package com.feamorx86.boardgame.test;

import com.feamorx86.boardgame.model.UserModel;
import com.feamorx86.boardgame.page.BasePage;
import com.feamorx86.boardgame.utils.HTMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Home on 28.08.2017.
 */
public class TestController extends BasePage {

    @Autowired
    ApplicationContext context;

   /* @Override
    public Object render(HttpServletRequest request, HttpServletResponse response, UserModel user) throws IOException, NoSuchMethodException {
        HTMLWriter writer = new HTMLWriter();
        writer.title("Test");
        try {
            FillTestData test = context.getBean(FillTestData.class);
            test.fillUsers();
            test.fillSections();
            writer.writeln("Test data was added");
        } catch (Exception ex) {
            ex.printStackTrace();
            writer.writeln("Fail to add test data");
            writer.writeMultiline(ex.toString());
        }
        response.getOutputStream().print(writer.toString());
        return null;
    }

    @Override
    public String getName() {
        return "test";
    }*/
}
