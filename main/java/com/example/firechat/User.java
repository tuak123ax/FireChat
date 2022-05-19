package com.example.firechat;

import java.io.Serializable;

public class User implements Serializable {
    public String uid;
    public String email;
    public String status;
    public String name;
    public String image;
    public User() {
    }

    public User(String uid, String email,String status,String name,String image) {
        this.uid = uid;
        this.email = email;
        this.status=status;
        this.name=name;
        this.image=image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
