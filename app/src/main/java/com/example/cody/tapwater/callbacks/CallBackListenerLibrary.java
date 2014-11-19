/****************************************************************************************
 /*
 /* FILE NAME: CallBackListenerLibrary.java
 /*
 /* DESCRIPTION: Callback interface for all callback methods associated with Library.
 /*
 /* REFERENCE: Called at the end of a particular Async method call.
 /*
 /* WRITTEN BY: Cody Rogers
 /* DATE: 11/19/14
 /*
 /****************************************************************************************/

package com.example.cody.tapwater.callbacks;

public interface CallBackListenerLibrary extends CallBack {

    /**
     * Called upon completion of LoadDrinksAsync.
     *
     * @param in:      integer response that indicates success or failure.
     * @param message: Server's response.
     */
    public void callbackLoadDrinks(Integer in, String message);

}