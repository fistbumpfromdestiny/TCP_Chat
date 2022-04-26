package com.company;

public class Protocol {

    private final int UNINITIALIZED = 0;
    private final int INITIALIZED = 1;

    private int currentState = 0;

    public Object inputHandler(String username, String inputLine) {

        Object output = null;

        if(currentState == UNINITIALIZED) {

            output = new ConnectionEstablisher();
            currentState = INITIALIZED;

        } else if(currentState == INITIALIZED) {

            if(!inputLine.isBlank())
                output = username + inputLine;
        }
        return output;
    }
}