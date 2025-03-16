package com.example.whatsinthefridge;

public class User {
    public String userId, name, email, profileImage;

    // Default constructor required for Firebase
    public User() {
    }

    public User(String userId, String name, String email, String profileImage) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }
}
