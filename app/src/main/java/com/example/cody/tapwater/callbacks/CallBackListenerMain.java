/****************************************************************************************
 /*
 /* FILE NAME: CallBackListenerMain.java
 /*
 /* DESCRIPTION: Callback interface for all callback methods associated with MainActivity.
 /*
 /* REFERENCE: Called at the end of a particular Async method call.
 /*
 /* WRITTEN BY: Cody Rogers
 /* DATE: 10/27/14
 /*
 /****************************************************************************************/

package com.example.cody.tapwater.callbacks;

import com.example.cody.tapwater.objects.Drink;

public interface CallBackListenerMain {

    /**
     * Called upon completion of CreateUserAsync.
     *
     * @param in: integer response that indicates success or failure.
     */
    public void callbackCreateUser(Integer in);

    /**
     * Called upon completion of CreateDrinkAsync.
     *
     * @param in: integer response that indicates success or failure.
     * @param d:  Drink object returned to determine category of created drink.
     */
    public void callbackCreateDrink(Integer in, Drink d);

    /**
     * Called upon completion of AuthenticateUserAsync.
     *
     * @param in: integer response that indicates success or failure.
     */
    public void callbackAuthenticateUser(Integer in);

    /**
     * Called upon completion of LoadDrinksAsync.
     *
     * @param in: integer response that indicates success or failure.
     */
    public void callbackLoadDrinks(Integer in);

}
