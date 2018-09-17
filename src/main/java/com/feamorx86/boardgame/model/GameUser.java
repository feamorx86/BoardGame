package com.feamorx86.boardgame.model;

import javax.persistence.*;

/**
 * Created by feamor on 04.09.2018.
 */
@Entity(name = "game_user")
@Table(name = "game_user")
public class GameUser {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "email",nullable = false)
    private String email;
    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "first_name",nullable = false)
    private String firstName;
    @Column(name = "last_name",nullable = true)
    private String lastName;
    @Column(name = "alias",nullable = true)
    private String alias;

    @Column(name = "registration_json",nullable = true)
    private String registrationJson;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getRegistrationJson() {
        return registrationJson;
    }

    public void setRegistrationJson(String registrationJson) {
        this.registrationJson = registrationJson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null) {
            this.password = password.toLowerCase();
        } else {
            password = null;
        }
    }
}
