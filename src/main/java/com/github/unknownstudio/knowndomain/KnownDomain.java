package com.github.unknownstudio.knowndomain;

import com.github.unknownstudio.knowndomain.game.GameMain;

public class KnownDomain {
    public static final int WIDTH = 550, HEIGHT = 400;
    private static GameMain main;

    public static void main(String[] args) {
        main = new GameMain(WIDTH, HEIGHT);
    }
}
