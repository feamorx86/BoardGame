package com.feamorx86.boardgame;

import com.feamorx86.boardgame.controller.GameManager;
import com.feamorx86.boardgame.controller.SessionController;
import com.feamorx86.boardgame.controller.Templates;
import com.feamorx86.boardgame.dao.*;
import com.feamorx86.boardgame.test.FillTestData;
import com.feamorx86.boardgame.test.SimpleTestTest;
import com.feamorx86.boardgame.test.TestController;
import com.feamorx86.boardgame.test.TestDisplayQuestion;
import com.feamorx86.boardgame.model.*;
import com.feamorx86.boardgame.page.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.session.*;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;
import java.io.File;

/**
 * Created by Home on 24.08.2017.
 */
@Configuration
@EnableWebMvc
@EnableTransactionManagement
@EnableSpringHttpSession
public class Config extends WebMvcConfigurerAdapter {

    @Bean
    public SessionFactory sessionFactory() {
        return new LocalSessionFactoryBuilder(getDataSource())
                /*.addAnnotatedClass(UserModel.class)
                .addAnnotatedClass(TextData.class)
                .addAnnotatedClass(Section.class)
                .addAnnotatedClass(SubSection.class)
                .addAnnotatedClass(Question.class)
                .addAnnotatedClass(Answer.class)
                .addAnnotatedClass(CorrectAnswer.class)
                .addAnnotatedClass(com.feamorx86.boardgame.model.Session.class)*/
//                .setProperty("hibernate.current_session_context_class", "thread")

                .addAnnotatedClass(UserModel.class)
                .addAnnotatedClass(TextData.class)
                .addAnnotatedClass(com.feamorx86.boardgame.model.Session.class)
                .addAnnotatedClass(GameUser.class)
                .addAnnotatedClass(GameType.class)
                .addAnnotatedClass(UserFriends.class)
                .addAnnotatedClass(CompleteGames.class)
                .addAnnotatedClass(FavoriteGames.class)

                .buildSessionFactory( );
    }

    @Bean
    public DataSource getDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:file:./src/main/resources/db/base.h2.db");
        config.addDataSourceProperty("characterEncoding","utf8");
        config.addDataSourceProperty("useUnicode","true");
        config.setUsername("feamor");
        config.setPassword("1979");
        config.setIdleTimeout(10);
        config.setMinimumIdle(2);
        config.setMaximumPoolSize(100);
        return new HikariDataSource(config);
    }


    @Bean(name = "transactionManager")
    public HibernateTransactionManager transactionManager(){
        return new HibernateTransactionManager(sessionFactory());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path1 = new File(".").getAbsolutePath();
        String webPath = "src/main/resources/web/";
        String path2 = new File(webPath).getAbsolutePath();
        System.out.println("app path : "+path1);
        System.out.println("web path : "+path2);
        registry
                .addResourceHandler("/web/**")
                .addResourceLocations("file:"+webPath);
        registry
                .addResourceHandler("/favicon.ico")
                .addResourceLocations("file:./src/favicon.ico");

    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public UserDAO getUserDAO(){
        return new UserDAO();
    }

    @Bean
    public GameUserDAO gameUserDAO() {
        return new GameUserDAO();
    }

    @Bean
    public GamesDAO gamesDAO() {
        return new GamesDAO();
    }

    @Bean
    public TextDAO getTextDAO() {
        return new TextDAO();
    }

    @Bean
    public SQLHelper sqlHelper() {
        return new SQLHelper();
    }


    //    //------------------------------------------------
//    //  Managers
    @Bean
    public RequestManager requestManager() {
        return new RequestManager();
    }


    @Bean
    public Templates templates() {
        return new Templates();
    }

    @Bean
    public GameManager gameManager() {
        return new GameManager();
    }

    @Bean
    public GameRegistrationPage gameRegistrationPage() {
        return new GameRegistrationPage();
    }

    @Bean
    public SimpleMainScreenPage simpleMainScreenPage() {
        return new SimpleMainScreenPage();
    }

    @Bean
    public AboutPage aboutPage() {
        return new AboutPage();
    }

    @Bean SQLPage sqlPage() {
        return new SQLPage();
    }

    @Bean
    public GameLogin gameLogin() {
        return new GameLogin();
    }

    @Bean
    public UserProfilePage userProfilePage() {
        return new UserProfilePage();
    }

    //------------------------------------------------------
    @Bean
    public FillTestData fillTestData() {
        return new FillTestData();
    }

    @Bean
    public SessionRepository sessionRepository() {
        return new SessionController();
    }
}
