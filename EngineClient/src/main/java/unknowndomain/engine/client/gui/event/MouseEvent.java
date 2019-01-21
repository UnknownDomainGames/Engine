package unknowndomain.engine.client.gui.event;

import unknowndomain.engine.client.input.keybinding.ActionMode;
import unknowndomain.engine.client.input.keybinding.Key;
import unknowndomain.engine.event.Event;

public class MouseEvent implements Event {

    protected MouseEvent(){
    }

    public static class MousePositionEvent extends MouseEvent{

        private double oldPosX;
        private double oldPosY;
        private double newPosX;
        private double newPosY;

        public MousePositionEvent(double oldPosX, double oldPosY, double newPosX, double newPosY) {
            this.oldPosX = oldPosX;
            this.oldPosY = oldPosY;

            this.newPosX = newPosX;
            this.newPosY = newPosY;
        }
    }

    public static class MouseMoveEvent extends MousePositionEvent{

        public MouseMoveEvent(double oldPosX, double oldPosY, double newPosX, double newPosY) {
            super(oldPosX, oldPosY, newPosX, newPosY);
        }
    }

    public static class MouseEnterEvent extends MousePositionEvent{

        public MouseEnterEvent(double oldPosX, double oldPosY, double newPosX, double newPosY) {
            super(oldPosX, oldPosY, newPosX, newPosY);
        }
    }

    public static class MouseLeaveEvent extends MousePositionEvent{

        public MouseLeaveEvent(double oldPosX, double oldPosY, double newPosX, double newPosY) {
            super(oldPosX, oldPosY, newPosX, newPosY);
        }
    }

    public static class MouseButtonEvent extends MouseEvent{

        private Key key;
        private ActionMode mode;
        private float posX;
        private float posY;

        public MouseButtonEvent(float posX, float posY, Key key, ActionMode mode) {
            this.key = key;
            this.mode = mode;
            this.posX = posX;
            this.posY = posY;
        }
    }

    public static class MouseClickEvent extends MouseButtonEvent{

        public MouseClickEvent(float posX, float posY, Key key) {
            super(posX, posY, key, ActionMode.PRESS);
        }
    }
    public static class MouseReleasedEvent extends MouseButtonEvent{

        public MouseReleasedEvent(float posX, float posY, Key key) {
            super(posX, posY, key, ActionMode.PRESS);
        }
    }

    public static class MouseWheelEvent extends MouseEvent{
        double xOffset;
        double yOffset;
        public MouseWheelEvent(double xOffset, double yOffset){
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
}
