package com.feamorx86.boardgame.controller;

import com.feamorx86.boardgame.dao.GameUserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * Created by Home on 25.08.2017.
 */
public class SessionController implements SessionRepository {

    private HashMap<String, Session> sessions =  new HashMap<>();

    @Autowired
    private GameUserDAO gameUserDAO;
    @Autowired

    @Override
    public Session createSession() {
        return new MapSession();
    }

    public Session createWithId(String sessionId) {
        return new MapSession(sessionId);
    }

    @PostConstruct
    public void loadSessions() {
        gameUserDAO.loadSessions();
    }

    @Override
    public void save(Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session getSession(String id) {
        Session session = sessions.get(id);
        if (session == null) {
            session = createWithId(id);
            save(session);
        }
        return session;
    }

    @Override
    public void delete(String id) {
        sessions.remove(id);
        gameUserDAO.logout(id);
    }
}
