package com.feamorx86.boardgame.dao;

import com.feamorx86.boardgame.model.Session;
import com.feamorx86.boardgame.model.UserModel;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by Home on 25.08.2017.
 */
@Repository
@Transactional
public class UserDAO {
    //private ArrayList<UserModel> allUsers = new ArrayList<UserModel>();
    //private HashMap<Integer, UserModel> usersById = new HashMap<Integer, UserModel>();
    //private HashMap<String, UserModel> usersBylogin = new HashMap<>();
    @Autowired
    private SessionFactory sessionFactory;

    public boolean add(UserModel user) {
        boolean result = false;
        if (user!=null) {
            if (!StringUtils.isEmpty(user.getLogin())) {
                Integer id = (Integer)sessionFactory.getCurrentSession().createSQLQuery("SELECT u.id FROM user AS u WHERE u.login like :new_login").setParameter("new_login", user.getLogin()).uniqueResult();
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

    public UserModel getUserForSession(String sessionId) {
        UserModel user = null;

        if (!StringUtils.isEmpty(sessionId)) {
            Session session = (Session) sessionFactory.getCurrentSession().createCriteria(Session.class).add(Restrictions.eq("sessionId", sessionId)).uniqueResult();
            if (session != null) {
                user = get(session.getUserId());
            }
        }
        return user;
    }

    public UserModel checkUser(String login, String password) {
        UserModel user = (UserModel) sessionFactory.getCurrentSession().createCriteria(UserModel.class).add(Restrictions.eq("login", login)).uniqueResult();
        UserModel result;
        if (user!= null && user.getPassword().equals(password)) {
            result = user;
        } else {
            result = null;
        }
        return result;
    }

    public List<UserModel> getAllUsers() {
        return (List<UserModel>) sessionFactory.getCurrentSession().createCriteria(UserModel.class).list();
    }

    public UserModel get(int userId) {
        UserModel user = (UserModel) sessionFactory.getCurrentSession().createCriteria(UserModel.class).add(Restrictions.eq("id", userId)).uniqueResult();
        return user;
    }

    public Session createSession(UserModel user, String sessionId) {
        Session session = new Session();
        session.setUserId(user.getId());
        session.setSessionId(sessionId);
        sessionFactory.getCurrentSession().save(session);
        return session;
    }

    public void logout(String session) {
        sessionFactory.getCurrentSession().createQuery("delete from session as s where s.sessionId=:ses").setString("ses", session).executeUpdate();
    }

    public void closeAllSessions(int userId) {
        sessionFactory.getCurrentSession().createQuery("delete from session as s where s.userId=:uid").setInteger("uid", userId).executeUpdate();
    }

    public boolean hasUserWithLogin(String login) {
        return sessionFactory.getCurrentSession().createCriteria(UserModel.class).add(Restrictions.eq("login", login)).uniqueResult() != null;
    }

    public boolean updateUser(UserModel userToEdit) {
        Integer id = (Integer) sessionFactory.getCurrentSession().createQuery("select id from user where login = :lg").setString("lg", userToEdit.getLogin()).setMaxResults(1).uniqueResult();
        boolean result;
        if (id != null) {
            if (id == userToEdit.getId()) {
                //login not changed
                sessionFactory.getCurrentSession().update(userToEdit);
                result = true;
            } else {
                result = false; //login used by another user
            }
        } else {
            //no user with such login
            sessionFactory.getCurrentSession().update(userToEdit);
            result = true;
        }
        return result;
     }

    public List<UserModel> findByField(String query) {
        List<UserModel> users = sessionFactory.getCurrentSession().createQuery("select * from user as u where u.firstName like :q or u.secondName like :q or u.therdName like :q or u.login like :q")
                .setParameter("q", query).list();
        return users;
    }

    public void deleteUser(UserModel userToDelete) {
        sessionFactory.getCurrentSession().delete(userToDelete);
    }
}
