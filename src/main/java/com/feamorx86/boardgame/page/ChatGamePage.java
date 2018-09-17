package com.feamorx86.boardgame.page;

import com.feamorx86.boardgame.Const;
import com.feamorx86.boardgame.controller.GameManager;
import com.feamorx86.boardgame.dao.SQLHelper;
import com.feamorx86.boardgame.games.BaseGame;
import com.feamorx86.boardgame.games.ChatGame;
import com.feamorx86.boardgame.games.Gamer;
import com.feamorx86.boardgame.model.GameUser;
import com.feamorx86.boardgame.utils.SimpleTagProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by feamor on 11.09.2018.
 */
public class ChatGamePage extends BasePage {

    @Autowired
    private SQLHelper sqlHelper;

    @Override
    public String getName() {
        return "games/chat";
    }

    @Autowired
    private GameManager manager;

    @Override
    public Object render(HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException {
        if (!isGet(request)) {
            displayNotSupported(response);
        } else {
            String action = request.getParameter("action");
            if (StringUtils.isEmpty(action)) {
                displayGameDescription(request, response, user);
            } else switch(action.toLowerCase()) {
                case "new_game":
                    createGame(user, request, response);
                    break;
                case "connect_to":
                    break;
                case "invite":
                    break;
                case "message":
                    break;
                case "has_changes":
                    break;
                case "history":
                    break;
                case "ping":
                    break;
                case "disconnect":
                    break;
                default:
                    displayNotSupported(response);
                    break;
            }
        }
        return null;
    }

    private void createGame(GameUser user, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //todo: check games;
        Gamer gamer = manager.getGamerByUser(user);
        if (gamer == null) {
            ChatGame game = new ChatGame(manager.generateGameId());
            вот тут надо разобраться что и как происходит, как сделать так чтобы 2 игры которые не могут идти паралельно не были бы случайно запущены,
            кроме того нужно решать как будет происходить иницитализация игры и её добавление и кто за это отвечает: игра менеджер или страница
                    ну и далее делать выполнение действий
            manager.getExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    game.initialize();
                    manager.addGame(game);
                    game.start();
                }
            });
            game.handleRequest(ChatGame.ActionTypes.ASSIGN_USER, request, response, gamer);
        } else {
            BaseGame oldGame = manager.isUserPlayGame(ChatGame.class, gamer, request);
            if (oldGame != null) {
                game.handleRequest(ChatGame.ActionTypes.ASSIGN_USER, request, response, gamer);
            } else {
                ChatGame game = new ChatGame();
            }
        }

    }

    private void displayNotSupported(HttpServletResponse response) throws IOException {
        response.getWriter().print(makeJsonError(300, "not supported"));
    }


    private void displayGameDescription(HttpServletRequest request, HttpServletResponse response, GameUser user) throws IOException {
        SimpleTagProcessor page = templates.getTemplate("chat_game_description");
        drawTemplate(page, response);
    }
}
