package com.feamorx86.boardgame.model;

import javax.persistence.*;

/**
 * Created by Home on 25.08.2017.
 */
@Entity(name = "question")
@Table(name = "question")
public class Question {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "type", nullable = false)
    private int type;
    @Column(name = "text_data_id", nullable = false)
    private int textDataId;
    @Column(name = "section_id", nullable = false)
    private int sectionId;
    @Column(name = "sub_section_id", nullable = false)
    private int subSectionId;
    @Column(name = "rate", nullable = true)
    private Float rate;

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

    public int getTextDataId() {
        return textDataId;
    }

    public void setTextDataId(int textDataId) {
        this.textDataId = textDataId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getSubSectionId() {
        return subSectionId;
    }

    public void setSubSectionId(int subSectionId) {
        this.subSectionId = subSectionId;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

}
