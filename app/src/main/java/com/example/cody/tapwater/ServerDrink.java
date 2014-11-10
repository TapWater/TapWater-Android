package com.example.cody.tapwater;

public class ServerDrink {

    public String device_token;
    public Drink drink;

    public ServerDrink(String dt, Drink d) {
        device_token = dt;
        drink = d;
    }
}
