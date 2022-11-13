package com.quintus.labs.datingapp.Main;



public class Cards {
    private String userId;
    private String name, profileImageUrl;
    private int age;


    public Cards(String userId, String name, String profileImageUrl, int age) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.age = age;
    }

    public Cards() {
    }

    public Cards(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }



    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
