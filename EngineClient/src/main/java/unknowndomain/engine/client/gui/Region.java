package unknowndomain.engine.client.gui;

import unknowndomain.engine.client.gui.renderer.ComponentRenderer;
import unknowndomain.engine.client.gui.renderer.RegionRenderer;

public class Region extends Container {

    private int minWidth = 0, minHeight = 0;
    private int prefWidth, prefHeight;
    private int maxWidth = Integer.MAX_VALUE, maxHeight = Integer.MAX_VALUE;

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
    public int minWidth() {
        return minWidth;
    }

    @Override
    public int minHeight() {
        return minHeight;
    }

    @Override
    public int prefWidth() {
        return prefWidth;
    }

    @Override
    public int prefHeight() {
        return prefHeight;
    }

    @Override
    public int maxWidth() {
        return maxWidth;
    }

    @Override
    public int maxHeight() {
        return maxHeight;
    }

    @Override
    protected ComponentRenderer<?> createDefaultRenderer() {
        return new RegionRenderer(this);
    }

    @Override
    public void layoutChildren() {
        for (Component component : getChildren()) {
            layoutInArea(component, component.getX(), component.getY(), component.prefWidth(), component.prefHeight());
        }
    }
}
