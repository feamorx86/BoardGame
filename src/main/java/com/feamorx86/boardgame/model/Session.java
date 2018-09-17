package com.feamorx86.boardgame.model;

import javax.persistence.*;

/**
 * Created by Home on 09.09.2017.
 */
@Entity(name = "session")
@Table(name = "session")
public class Session {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "session_id", nullable = false, unique = true, length = 255)
    private String sessionId;
    @Column(name = "user_id", nullable = false)
    private int userId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
