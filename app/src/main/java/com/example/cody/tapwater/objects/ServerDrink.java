/****************************************************************************************
 /*
 /* FILE NAME: ServerDrink.java
 /*
 /* DESCRIPTION: Drink created to send to server with proper json formatting.
 /*
 /* REFERENCE: Used when POSTing Drink.
 /*
 /* WRITTEN BY: Cody Rogers
 /* DATE: 10/27/14
 /*
 /****************************************************************************************/

package com.example.cody.tapwater.objects;

public class ServerDrink {

    public String device_token;
    public Drink drink;

    /**
     * Constructor.
     *
     * @param dt: Device token associated with device sending request. Obtained during login.
     * @param d:  Drink to POST.
     */
    public ServerDrink(String dt, Drink d) {
        device_token = dt;
        drink = d;
    }
}
