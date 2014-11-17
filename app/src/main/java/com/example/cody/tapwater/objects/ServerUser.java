/****************************************************************************************
 /*
 /* FILE NAME: ServerUser.java
 /*
 /* DESCRIPTION: User created to send to server with proper json formatting.
 /*
 /* REFERENCE: Used when POSTing User.
 /*
 /* WRITTEN BY: Cody Rogers
 /* DATE: 10/27/14
 /*
 /****************************************************************************************/

package com.example.cody.tapwater.objects;

public class ServerUser {

    public User user;

    /**
     * Constructor.
     *
     * @param u: User to POST.
     */
    public ServerUser(User u) {
        user = u;
    }
}
