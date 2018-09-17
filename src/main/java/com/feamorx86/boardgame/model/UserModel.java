package com.feamorx86.boardgame.model;


import javax.persistence.*;

/**
 * Created by Home on 25.08.2017.
 */

@Entity(name = "user")
@Table(name = "user")
public class UserModel {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "type", nullable = false)
    private int type;

    @Column(name = "first_name", nullable = false, length = 200)
    private String firstName;
    @Column(name = "second_name", nullable = false, length = 200)
    private String secondName;
    @Column(name = "third_name", nullable = true, length = 200)
    private String thirdName;

    @Column(name = "login", nullable = true, length = 200)
    private String login;
    @Column(name = "password", nullable = true, length = 200)
    private String password;
    @Column(name = "tag", nullable = true, length = 200)
    private String tag;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getThirdName() {
        return thirdName;
    }

    public void setThirdName(String thirdName) {
        this.thirdName = thirdName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
