package com.example.cody.tapwater.callbacks;

import com.example.cody.tapwater.objects.Drink;

public interface CallBackListenerMain {

    public void callbackCreateUser(Integer in);

    public void callbackCreateDrink(Integer in, Drink d);

    public void callbackAuthenticateUser(Integer in);

    public void callbackLoadDrinks(Integer in);

}
