package com.jeeps.datavisualizer.model;

/**
 * Created by jeeps on 11/15/2017.
 */

public class User {
    private String mAgent;

    public User() {}

    public User(String agent) {
        mAgent = agent;
    }

    public String getAgent() {
        return mAgent;
    }

    public void setAgent(String agent) {
        mAgent = agent;
    }
}
