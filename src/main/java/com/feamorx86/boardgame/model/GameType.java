package com.feamorx86.boardgame.model;

import javax.persistence.*;

/**
 * Created by feamor on 04.09.2018.
 */
@Entity(name = "game_type")
@Table(name = "game_type")
public class GameType {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "app_type", nullable = false)
    private int appType;
    @Column(name = "name", nullable = true)
    private String name;
    @Column(name = "description", nullable = true)
    private String description;
    @Column(name = "tags", nullable = true)
    private String tags;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
