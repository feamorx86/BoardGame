package com.feamorx86.boardgame.model;

import javax.persistence.*;

/**
 * Created by feamor on 04.09.2018.
 */
@Entity(name = "friends")
@Table(name = "friends")
public class UserFriends {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "owner_id",nullable = false)
    private int ownerId;

    @Column(name = "friend_id",nullable = false)
    private int friendId;

    @Column(name = "tags",nullable = true)
    private String tags;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}