package com.feamorx86.boardgame.dao;

import com.feamorx86.boardgame.model.GameType;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by feamor on 11.09.2018.
 */

@Repository
@Transactional
public class GamesDAO {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private GameUserDAO gameUserDAO;

    public List<GameType> loadGameTypes() {
        return sessionFactory.getCurrentSession().createCriteria(GameType.class).list();
    }


}
