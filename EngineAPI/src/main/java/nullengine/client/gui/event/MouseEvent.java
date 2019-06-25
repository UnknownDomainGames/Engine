package nullengine.client.gui.event;

import nullengine.client.gui.Component;
import nullengine.client.input.keybinding.ActionMode;
import nullengine.client.input.keybinding.Key;

public class MouseEvent extends ComponentEvent {

    protected MouseEvent(Component component) {
        super(component);
    }

    public static class MousePositionEvent extends MouseEvent {

        private double oldPosX;
        private double oldPosY;
        private double newPosX;
        private double newPosY;

        public MousePositionEvent(Component component, double oldPosX, double oldPosY, double newPosX, double newPosY) {
            super(component);
            this.oldPosX = oldPosX;
            this.oldPosY = oldPosY;

            this.newPosX = newPosX;
            this.newPosY = newPosY;
        }

        public double getOldPosX() {
            return oldPosX;
        }

        public double getOldPosY() {
            return oldPosY;
        }

        public double getNewPosX() {
            return newPosX;
        }

        public double getNewPosY() {
            return newPosY;
        }
    }

    public static class MouseMoveEvent extends MousePositionEvent {

        public MouseMoveEvent(Component component, double oldPosX, double oldPosY, double newPosX, double newPosY) {
            super(component, oldPosX, oldPosY, newPosX, newPosY);
        }
    }

    public static class MouseEnterEvent extends MousePositionEvent {

        public MouseEnterEvent(Component component, double oldPosX, double oldPosY, double newPosX, double newPosY) {
            super(component, oldPosX, oldPosY, newPosX, newPosY);
        }
    }

    public static class MouseLeaveEvent extends MousePositionEvent {

        public MouseLeaveEvent(Component component, double oldPosX, double oldPosY, double newPosX, double newPosY) {
            super(component, oldPosX, oldPosY, newPosX, newPosY);
        }
    }

    public static class MouseButtonEvent extends MouseEvent {

        private Key key;
        private ActionMode mode;
        private float posX;
        private float posY;

        public MouseButtonEvent(Component component, float posX, float posY, Key key, ActionMode mode) {
            super(component);
            this.key = key;
            this.mode = mode;
            this.posX = posX;
            this.posY = posY;
        }

        public Key getKey() {
            return key;
        }

        public ActionMode getMode() {
            return mode;
        }

        public float getPosX() {
            return posX;
        }

        public float getPosY() {
            return posY;
        }
    }

    public static class MouseClickEvent extends MouseButtonEvent {

        public MouseClickEvent(Component component, float posX, float posY, Key key) {
            super(component, posX, posY, key, ActionMode.PRESS);
        }
    }

    public static class MouseReleasedEvent extends MouseButtonEvent {

        public MouseReleasedEvent(Component component, float posX, float posY, Key key) {
            super(component, posX, posY, key, ActionMode.PRESS);
        }
    }

    public static class MouseHoldEvent extends MouseButtonEvent {

        public MouseHoldEvent(Component component, float posX, float posY, Key key) {
            super(component, posX, posY, key, ActionMode.PRESS);
        }
    }

    public static class MouseWheelEvent extends MouseEvent {
        double xOffset;
        double yOffset;

        public MouseWheelEvent(Component component, double xOffset, double yOffset) {
            super(component);
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
}
