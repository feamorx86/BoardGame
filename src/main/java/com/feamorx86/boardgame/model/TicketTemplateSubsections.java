package com.feamorx86.boardgame.model;

/**
 * Created by Home on 25.08.2017.
 */
public class TicketTemplateSubsections {
    private int id;
    private int ticketId;
    private int subSectionId;
    private int minQuestionsCount;
    private Integer maxQuestionsCount;

    private Integer minQuestionRate;
    private Integer maxQuestionRate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getSubSectionId() {
        return subSectionId;
    }

    public void setSubSectionId(int subSectionId) {
        this.subSectionId = subSectionId;
    }

    public int getMinQuestionsCount() {
        return minQuestionsCount;
    }

    public void setMinQuestionsCount(int minQuestionsCount) {
        this.minQuestionsCount = minQuestionsCount;
    }

    public Integer getMaxQuestionsCount() {
        return maxQuestionsCount;
    }

    public void setMaxQuestionsCount(Integer maxQuestionsCount) {
        this.maxQuestionsCount = maxQuestionsCount;
    }

    public Integer getMinQuestionRate() {
        return minQuestionRate;
    }

    public void setMinQuestionRate(Integer minQuestionRate) {
        this.minQuestionRate = minQuestionRate;
    }

    public Integer getMaxQuestionRate() {
        return maxQuestionRate;
    }

    public void setMaxQuestionRate(Integer maxQuestionRate) {
        this.maxQuestionRate = maxQuestionRate;
    }
}
