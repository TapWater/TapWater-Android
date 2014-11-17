/****************************************************************************************
 /*
 /* FILE NAME: User.java
 /*
 /* DESCRIPTION: User object used for DB and server interaction. Foundational to application.
 /*
 /* REFERENCE: Used throughout application for DB and server interaction.
 /*
 /* WRITTEN BY: Cody Rogers
 /* DATE: 10/24/14
 /*
 /****************************************************************************************/

package com.example.cody.tapwater.objects;

public class User {

    private String username;
    private String password;
    private String password_confirmation;
    private String device_token;
    public int logged_in;

    /**
     * Empty Constructor.
     */
    public User() {
    }

    /**
     *
     * @return Username.
     *
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * Set Username.
     *
     * @param u: passed Username.
     */
    public void setUsername(String u) {
        username = u;
    }

    /**
     *
     * @return Password.
     *
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * Set Password.
     *
     * @param p: passed Password.
     */
    public void setPassword(String p) {
        password = p;
    }

    /**
     *
     * @return Password Confirmation.
     *
     */
    public String getPasswordConfirmation() {
        return password_confirmation;
    }

    /**
     *
     * Set Password Confirmation.
     *
     * @param pc: Passed Password Confirmation.
     */
    public void setPasswordConfirmation(String pc) {
        password_confirmation = pc;
    }

    /**
     *
     * @return Device Token
     *
     */
    public String getDeviceToken() {
        return device_token;
    }

    /**
     *
     * Set Device Token.
     *
     * @param d: passed Device Token.
     */
    public void setDeviceToken(String d) {
        device_token = d;
    }

    /**
     *
     * @return Logged In state.
     *
     */
    public int getLoggedIn() {
        return logged_in;
    }

    /**
     *
     * Set Logged In state.
     *
     * @param l: passed login state.
     */
    public void setLoggedIn(int l) {
        logged_in = l;
    }

    /**
     *
     * String representation of User.
     *
     * @return string displaying fields of User.
     */
    @Override
    public String toString() {
        return username + " " + password + " " + password_confirmation + " " + device_token + " " + logged_in;
    }
}
