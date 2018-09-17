package com.feamorx86.boardgame.model;

import javax.persistence.*;

/**
 * Created by feamor on 04.09.2018.
 */
@Entity(name = "complete_games")
@Table(name = "complete_games")
public class CompleteGames {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "game_type_id", nullable = false)
    private int gameTypeId;
    @Column(name = "game_results", nullable = true)
    private String gameResults;
    @Column(name = "base_statistics", nullable = true)
    private String baseStatistics;
    @Column(name = "additional_statistics", nullable = true)
    private String additionalStatistics;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGameTypeId() {
        return gameTypeId;
    }

    public void setGameTypeId(int gameTypeId) {
        this.gameTypeId = gameTypeId;
    }

    public String getGameResults() {
        return gameResults;
    }

    public void setGameResults(String gameResults) {
        this.gameResults = gameResults;
    }

    public String getBaseStatistics() {
        return baseStatistics;
    }

    public void setBaseStatistics(String baseStatistics) {
        this.baseStatistics = baseStatistics;
    }

    public String getAdditionalStatistics() {
        return additionalStatistics;
    }

    public void setAdditionalStatistics(String additionalStatistics) {
        this.additionalStatistics = additionalStatistics;
    }
}
