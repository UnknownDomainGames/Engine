package com.github.unknownstudio.unknowndomain.engineapi.game;

public interface Game {

    /**
     * method to initialize the Game
     */
    void init();

    /**
     * method executed in a game loop
     */
    void loop();

    /**
     * method called in termination
     */
    void terminate();
}
