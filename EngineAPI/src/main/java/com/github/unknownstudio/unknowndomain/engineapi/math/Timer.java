package com.github.unknownstudio.unknowndomain.engineapi.math;

public class Timer {
    private double lastTime;

    public void init(){
        lastTime = getTime();
    }

    public double getTime() {
        return System.nanoTime() / 1_000_000_000.0;
    }

    public float getElapsedTime(){
        double time = getTime();
        float elapsedtime = (float) (time - lastTime);
        lastTime = time;
        return elapsedtime;
    }

    public double getLastLoopTime() {
        return lastTime;
    }
}
