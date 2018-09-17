package com.feamorx86.boardgame.controller;

import com.feamorx86.boardgame.dao.GamesDAO;
import com.feamorx86.boardgame.games.BaseGame;
import com.feamorx86.boardgame.games.ChatGame;
import com.feamorx86.boardgame.games.Gamer;
import com.feamorx86.boardgame.model.GameType;
import com.feamorx86.boardgame.model.GameUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by feamor on 11.09.2018.
 */

@Component
public class GameManager {
    private HashMap<Class, Integer> gameTypesByClass = new HashMap<>();
    private HashMap<Integer, GameType> gameTypes = new HashMap<>();

    private HashMap<Integer, BaseGame> activeGames = new HashMap<>();
    private HashMap<Integer, ArrayList<BaseGame>> activeGamesByType = new HashMap<>();
    private HashMap<Integer, Gamer> gamersByUserId = new HashMap<>();
    private ExecutorService executor;

    private AtomicInteger gameIdGenerator = new AtomicInteger();

    @Autowired
    private GamesDAO gamesDAO;

    @PostConstruct
    public void loadGameTypes() {
        gameTypes.clear();
        for(GameType type : gamesDAO.loadGameTypes()) {
            gameTypes.put(type.getAppType(), type);
        }
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public int generateGameId() {
        return gameIdGenerator.incrementAndGet();
    }

    public void start() {
        executor = Executors.newFixedThreadPool(4);
    }

    public void stop() {
        executor.shutdown();
    }

    public void pingGame(BaseGame game) {
        executor.submit(new RunGameUpdate(game));
    }

    public Gamer getGamerByUser(GameUser user) {
        synchronized (gamersByUserId) {
            return gamersByUserId.get(user.getId());
        }
    }

    public BaseGame isUserPlayGame(Class gameClass, Gamer gamer, HttpServletRequest request) {
        BaseGame result = null;
        synchronized (this) {
            Integer gameTypeId = gameTypesByClass.get(gameClass);
            if (gameTypeId != null) {
                ArrayList<BaseGame> games = activeGamesByType.get(gameTypeId);
                if (games != null) {
                    for(BaseGame game : games) {
                        if (game.isActive() && game.hasActiveGamer(gamer)) {
                            result = game;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    public void addGame(BaseGame game) {
        synchronized (this) {
            activeGames.put(game.getId(), game);
            ArrayList<BaseGame> games = activeGamesByType.get(game.getGameType().getId());
            if (games == null) {
                games
            }
        }
    }

    public static class RunGameUpdate implements Runnable {

        private BaseGame game;

        public RunGameUpdate(BaseGame game) {
            this.game = game;
        }

        @Override
        public void run() {
            try {
                game.update();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
