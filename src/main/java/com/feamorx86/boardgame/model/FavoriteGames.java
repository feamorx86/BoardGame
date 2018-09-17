package com.feamorx86.boardgame.model;

import javax.persistence.*;

/**
 * Created by feamor on 04.09.2018.
 */
@Entity(name = "favorite_games")
@Table(name = "favorite_games")
public class FavoriteGames {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "owner_id", nullable = false)
    private int userId;
    @Column(name = "game_type_id", nullable = false)
    private int gameTypeId;
    @Column(name = "tags", nullable = true)
    private String tags;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(int gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
