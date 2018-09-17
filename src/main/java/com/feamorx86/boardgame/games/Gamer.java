package com.feamorx86.boardgame.games;

import com.feamorx86.boardgame.model.GameUser;

/**
 * Created by feamor on 11.09.2018.
 */
public class Gamer {
    private GameUser user;
    private int state;
    private int inGameId;

    public GameUser getUser() {
        return user;
    }

    public void setUser(GameUser user) {
        this.user = user;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getInGameId() {
        return inGameId;
    }

    public void setInGameId(int inGameId) {
        this.inGameId = inGameId;
    }
}
