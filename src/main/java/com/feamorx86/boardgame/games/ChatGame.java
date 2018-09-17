package com.feamorx86.boardgame.games;

/**
 * Created by feamor on 11.09.2018.
 */
public class ChatGame extends BaseGame {

    public static class ActionTypes {
        public static final int ASSIGN_USER = 100;
        public static final int EXIST_USER_ASK_CREATE_GAME = 101;
    }

    public ChatGame(int id) {
        super(id);
    }
}
