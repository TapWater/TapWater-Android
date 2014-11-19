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

public interface CallBackListenerMain extends CallBack {

    /**
     * Called upon completion of CreateUserAsync.
     *
     * @param in: integer response that indicates success or failure.
     * @param message: Server's response.
     */
    public void callbackCreateUser(Integer in, String message);

    /**
     * Called upon completion of CreateDrinkAsync.
     *
     * @param in: integer response that indicates success or failure.
     * @param d:  Drink object returned to determine category of created drink.
     * @param message: Server's response.
     */
    public void callbackCreateDrink(Integer in, Drink d, String message);

    /**
     * Called upon completion of AuthenticateUserAsync.
     *
     * @param in: integer response that indicates success or failure.
     * @param message: Server's response.
     */
    public void callbackAuthenticateUser(Integer in, String message);

    /**
     * Called upon completion of LoadDrinksAsync.
     *
     * @param in: integer response that indicates success or failure.
     * @param message: Server's response.
     */
    public void callbackLoadDrinks(Integer in, String message);

}
