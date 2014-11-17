/****************************************************************************************
 /*
 /* FILE NAME: Drinks.java
 /*
 /* DESCRIPTION: Used to send an ArrayList of Drinks to the server with proper json formatting.
 /*
 /* REFERENCE: Used when POSTing or GETing a list of Drinks.
 /*
 /* WRITTEN BY: Cody Rogers
 /* DATE: 10/27/14
 /*
 /****************************************************************************************/

package com.example.cody.tapwater.objects;

import java.util.List;

public class Drinks {

    public List<Drink> drinks;

    /**
     * Constructor for Drinks List.
     *
     * @param d: Drinks passed.
     */
    public Drinks(List<Drink> d) {
        drinks = d;
    }
}
