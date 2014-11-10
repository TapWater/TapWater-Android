package com.example.cody.tapwater;

public interface CallBackListenerMain {

    public void callbackCreateUser(Integer in);

    public void callbackCreateDrink(Integer in, Drink d);

    public void callbackAuthenticateUser(Integer in);

    public void callbackLoadDrinks(Integer in);

}
