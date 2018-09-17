package com.feamorx86.boardgame.model;

import javax.persistence.*;

/**
 * Created by Home on 25.08.2017.
 */
@Entity(name = "sub_section")
@Table(name = "sub_section")
public class SubSection {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "title", nullable = false, length = 1000)
    private String title;
    @Column(name = "subtitle", nullable = true, length = 1000)
    private String subtitle;
    @Column(name = "comment", nullable = true, length = 1000)
    private String comment;

    @Column(name = "section_id", nullable = false)
    private int sectionId;

    @Column(nullable = false)
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

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
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
