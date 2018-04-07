package com.github.unknownstudio.knowndomain.game.key;

import com.github.unknownstudio.knowndomain.game.GameMain;

public abstract class KeyBinding {
    abstract public int BindKey();
    abstract public int BindAction();
    abstract public void handle(GameMain main);
}
