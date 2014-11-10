package com.example.cody.tapwater.objects;

public class User {

    private String username;
    private String password;
    private String password_confirmation;
    private String device_token;
    public int logged_in;

    public User() {
    }

    public User(String u, String p) {
        username = u;
        password = p;
        password_confirmation = "";
        device_token = "";
    }

    public User(String u, String p, String pc) {
        username = u;
        password = p;
        password_confirmation = pc;
        device_token = "";
    }

    public User(String u, String d, int l) {
        username = u;
        password = "";
        password_confirmation = "";
        device_token = d;
        logged_in = l;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String u) {
        username = u;
    }

    public String getPassword() {
        return username;
    }

    public void setPassword(String p) {
        password = p;
    }

    public String getPasswordConfirmation() {
        return password_confirmation;
    }

    public void setPasswordConfirmation(String pc) {
        password_confirmation = pc;
    }

    public String getDeviceToken() {
        return device_token;
    }

    public void setDeviceToken(String d) {
        device_token = d;
    }

    public int getLoggedIn() {
        return logged_in;
    }

    public void setLoggedIn(int l) {
        logged_in = l;
    }

    @Override
    public String toString() {
        return username + " " + password + " " + password_confirmation + " " + device_token + " " + logged_in;
    }
}
