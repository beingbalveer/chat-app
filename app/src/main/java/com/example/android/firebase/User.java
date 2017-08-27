package com.example.android.firebase;


import java.sql.Struct;

public class User {

    public String name = "";
    public String email = "";
    public String phone = "";
    public String profilePic = "";
    public String status = "";

    public User(){}

    public User(String email, String name)
    {
        this.email = email;
        this.name = name;
    }

    public User(String name,String email, String phone, String profilePic)
    {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profilePic = profilePic;
    }
}
