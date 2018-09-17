package com.feamorx86.boardgame.model;

import javax.persistence.*;

/**
 * Created by Home on 25.08.2017.
 */
@Entity(name = "text_data")
@Table(name = "text_data")
public class TextData {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "type", nullable = false)
    private int type;
    @Column(name = "text", nullable = false, length = 2000)
    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
