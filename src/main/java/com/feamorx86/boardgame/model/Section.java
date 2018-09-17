package com.feamorx86.boardgame.model;

import javax.persistence.*;

/**
 * Created by Home on 25.08.2017.
 */
@Entity(name = "section")
@Table(name = "section")
public class Section {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "title", nullable = false, length = 1000)
    private String title;
    @Column(name = "description", nullable = true, length = 1000)
    private String description;
    @Column(name = "comment", nullable = true, length = 1000)
    private String comment;
    @Column(name = "position", nullable = false)
    private int position;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
