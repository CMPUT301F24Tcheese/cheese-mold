package com.example.myapplication.objects;

public class Users {
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicture;
    private String role;

    public Users(String firstName, String lastName, String email, String profilePicture, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profilePicture = profilePicture;
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return firstName + ' ' + lastName;
    }
}
