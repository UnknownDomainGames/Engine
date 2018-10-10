package unknowndomain.engine.client.gui;

import unknowndomain.engine.client.gui.renderer.ComponentRenderer;
import unknowndomain.engine.client.gui.renderer.RegionRenderer;

public class Region extends Container {

    private int minWidth, minHeight;
    private int prefWidth, prefHeight;
    private int maxWidth, maxHeight;

    public int getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    public int getPrefWidth() {
        return prefWidth;
    }

    public void setPrefWidth(int prefWidth) {
        this.prefWidth = prefWidth;
    }

    public int getPrefHeight() {
        return prefHeight;
    }

    public void setPrefHeight(int prefHeight) {
        this.prefHeight = prefHeight;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    @Override
    public int prefWidth() {
        return 0;
    }

    @Override
    public int prefHeight() {
        return 0;
    }

    @Override
    protected ComponentRenderer<?> createDefaultRenderer() {
        return new RegionRenderer(this);
    }

    @Override
    public void layoutChildren() {

    }
}
