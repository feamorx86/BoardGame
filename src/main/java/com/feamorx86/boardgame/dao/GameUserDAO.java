package com.feamorx86.boardgame.dao;

import com.feamorx86.boardgame.controller.SessionController;
import com.feamorx86.boardgame.model.GameUser;
import com.feamorx86.boardgame.model.Session;
import com.feamorx86.boardgame.model.UserModel;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.persistence.PostLoad;
import javax.xml.bind.DatatypeConverter;
import java.util.List;

/**
 * Created by feamor on 04.09.2018.
 */
@Repository
@Transactional
public class GameUserDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private SessionRepository sessionRepository;

    public void loadSessions() {
        for(Session session : (List<Session>)sessionFactory.getCurrentSession().createCriteria(Session.class).list()) {
            org.springframework.session.Session httpSession = sessionRepository.getSession(session.getSessionId());
            httpSession.setAttribute("user_id", session.getUserId());
        }
    }

    public boolean add(GameUser user) {
        boolean result = false;
        if (user!=null) {
            if (!StringUtils.isEmpty(user.getEmail())) {
                Integer id = (Integer)sessionFactory.getCurrentSession().createSQLQuery("SELECT u.id FROM game_user AS u WHERE u.email like :new_email").setParameter("new_email", user.getEmail()).uniqueResult();
                if (id == null || user.getId() == id.intValue()) {
                    sessionFactory.getCurrentSession().save(user);
                    result = true;
                }
            } else {
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }

    public GameUser getUserForSession(String sessionId) {
        GameUser user = null;

        if (!StringUtils.isEmpty(sessionId)) {
            Session session = (Session) sessionFactory.getCurrentSession().createCriteria(Session.class).add(Restrictions.eq("sessionId", sessionId)).uniqueResult();
            if (session != null) {
                user = get(session.getUserId());
            }
        }
        return user;
    }

    public GameUser checkUser(String email, String password) {
        GameUser user = (GameUser) sessionFactory.getCurrentSession().createCriteria(GameUser.class).add(Restrictions.eq("email", email.toLowerCase())).uniqueResult();
        GameUser result;
        if (user!= null && user.getPassword().equals(password)) {
            result = user;
        } else {
            result = null;
        }
        return result;
    }

    public List<GameUser> getAllUsers() {
        return (List<GameUser>) sessionFactory.getCurrentSession().createCriteria(GameUser.class).list();
    }

    public GameUser get(int userId) {
        GameUser user = (GameUser) sessionFactory.getCurrentSession().createCriteria(GameUser.class).add(Restrictions.eq("id", userId)).uniqueResult();
        return user;
    }

    public Session createSession(GameUser user, String sessionId) {
        Session session = (Session) sessionFactory.getCurrentSession().createCriteria(Session.class).add(Restrictions.eq("sessionId", sessionId)).uniqueResult();
        if (session == null) {
            session = new Session();
            session.setUserId(user.getId());
            session.setSessionId(sessionId);
            sessionFactory.getCurrentSession().save(session);
        } else {
            if (session.getUserId() != user.getId()) {
                //TODO: log warnings
                session.setUserId(user.getId());
                sessionFactory.getCurrentSession().save(session);
            }
        }
        return session;
    }

    public boolean logout(String session) {
        int updated = sessionFactory.getCurrentSession().createQuery("delete from session as s where s.sessionId=:ses").setString("ses", session).executeUpdate();
        return updated > 0;
    }

    public void closeAllSessions(int userId) {
        sessionFactory.getCurrentSession().createQuery("delete from session as s where s.userId=:uid").setInteger("uid", userId).executeUpdate();
    }

//    public boolean hasUserWithLogin(String login) {
//        return sessionFactory.getCurrentSession().createCriteria(UserModel.class).add(Restrictions.eq("login", login)).uniqueResult() != null;
//    }

    public void saveUser(GameUser userToEdit) {
        sessionFactory.getCurrentSession().save(userToEdit);
    }

    public void updateUser(GameUser userToEdit) {
        sessionFactory.getCurrentSession().update(userToEdit);
    }


    public List<GameUser> findByField(String query) {
        List<GameUser> users = sessionFactory.getCurrentSession()
                .createQuery("select * from game_user as u where u.firstName like :q or u.secondName like :q or u.alias like :q")
                .setParameter("q", query).list();
        return users;
    }

    public void deleteUser(GameUser userToDelete) {
        sessionFactory.getCurrentSession().delete(userToDelete);
    }

    public boolean hasUserWithEmail(String email) {
        //sessionFactory.getCurrentSession().createSQLQuery("SELECT u.id FROM game_user AS u WHERE u.email like :new_email").setParameter("new_email", email).uniqueResult();
        return sessionFactory.getCurrentSession().createCriteria(GameUser.class).add(Restrictions.eq("email", email.trim().toLowerCase())).uniqueResult() != null;
    }

    public GameUser checkUserMd5(String email, String passwordMd5) {
        GameUser user = (GameUser) sessionFactory.getCurrentSession().createCriteria(GameUser.class).add(Restrictions.eq("email", email.toLowerCase())).uniqueResult();
        if (user != null) {
            byte[] digest = DigestUtils.md5Digest(user.getPassword().getBytes());
            String md5 = DatatypeConverter.printHexBinary(digest).toUpperCase();
            if (!md5.equalsIgnoreCase(passwordMd5)) {
                user = null;
            }
        }
        return user;
    }
}
