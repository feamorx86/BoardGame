package com.feamorx86.boardgame.games;

import com.feamorx86.boardgame.controller.GameManager;
import com.feamorx86.boardgame.model.GameType;
import com.feamorx86.boardgame.model.GameUser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by feamor on 11.09.2018.
 */

public class BaseGame {
    private int id;
    @Autowired
    protected GameManager gameManager;
    protected GameType gameType;
    protected ArrayList<InGameAction> currentUpdate, currentHandle;

    protected HashMap<Integer, Gamer> gamers = new HashMap<>();

    public BaseGame(int id) {
        currentHandle = new ArrayList<>();
        currentUpdate = new ArrayList<>();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private Object updateLocker = new Object();

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public HashMap<Integer, Gamer> getGamers() {
        return gamers;
    }

    public void update() {
        synchronized (updateLocker) {
            boolean hasActions;
            synchronized (this) {
                if (currentUpdate.size() == 0) {
                    if (currentHandle.size() > 0) {
                        //swap lists
                        ArrayList<InGameAction> temp;
                        temp = currentUpdate;
                        currentUpdate = currentHandle;
                        currentHandle = temp;
                        hasActions = true;
                    } else {
                        hasActions = false;
                    }
                } else {
                    hasActions = true;
                }
            }
            if (hasActions) {
                while (currentUpdate.size() > 0) {
                    InGameAction action = currentUpdate.remove(0);
                    processAction(action);
                }
            }
        }
    }

    protected int processAction(InGameAction action) {
        return -1;
    }

    protected int processRequest(InGameAction action) {
        return -1;
    }

    private AtomicInteger actionIdGenerator = new AtomicInteger();

    protected boolean initialized = false;

    protected int nextActionId() {
        return actionIdGenerator.incrementAndGet();
    }

    public void initialize() {

    }

    public void start() {
        initialized = true;
        gameManager.pingGame(this);
    }

    public Object handleRequest(int type, HttpServletRequest request, HttpServletResponse response, Gamer gamer) {
        InGameAction action = new InGameAction(nextActionId());
        action.setType(type);
        action.setGamer(gamer);
        action.setRequest(request);
        action.setResponse(response);
        processAction(action);
        synchronized (this) {
            currentHandle.add(action);
        }
        if (initialized) {
            gameManager.pingGame(this);
        }
        return null;
    }

    public boolean isActive() {
        return false;
    }

    public boolean hasActiveGamer(Gamer gamer) {
        return gamers.containsKey(gamer.getUser().getId());
    }
}
