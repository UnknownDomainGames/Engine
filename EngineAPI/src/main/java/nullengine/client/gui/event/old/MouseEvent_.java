package nullengine.client.gui.event.old;

import nullengine.client.gui.Node;
import nullengine.client.input.keybinding.ActionMode;
import nullengine.client.input.keybinding.Key;

@Deprecated
public class MouseEvent_ extends ComponentEvent {

    protected MouseEvent_(Node node) {
        super(node);
    }

    public static class MousePositionEvent extends MouseEvent_ {

        private double oldPosX;
        private double oldPosY;
        private double newPosX;
        private double newPosY;

        public MousePositionEvent(Node node, double oldPosX, double oldPosY, double newPosX, double newPosY) {
            super(node);
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

        public MouseMoveEvent(Node node, double oldPosX, double oldPosY, double newPosX, double newPosY) {
            super(node, oldPosX, oldPosY, newPosX, newPosY);
        }
    }

    public static class MouseEnterEvent extends MousePositionEvent {

        public MouseEnterEvent(Node node, double oldPosX, double oldPosY, double newPosX, double newPosY) {
            super(node, oldPosX, oldPosY, newPosX, newPosY);
        }
    }

    public static class MouseLeaveEvent extends MousePositionEvent {

        public MouseLeaveEvent(Node node, double oldPosX, double oldPosY, double newPosX, double newPosY) {
            super(node, oldPosX, oldPosY, newPosX, newPosY);
        }
    }

    public static class MouseButtonEvent extends MouseEvent_ {

        private Key key;
        private ActionMode mode;
        private float posX;
        private float posY;

        public MouseButtonEvent(Node node, float posX, float posY, Key key, ActionMode mode) {
            super(node);
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

        public MouseClickEvent(Node node, float posX, float posY, Key key) {
            super(node, posX, posY, key, ActionMode.PRESS);
        }
    }

    public static class MouseReleasedEvent extends MouseButtonEvent {

        public MouseReleasedEvent(Node node, float posX, float posY, Key key) {
            super(node, posX, posY, key, ActionMode.PRESS);
        }
    }

    public static class MouseHoldEvent extends MouseButtonEvent {

        public MouseHoldEvent(Node node, float posX, float posY, Key key) {
            super(node, posX, posY, key, ActionMode.PRESS);
        }
    }

    public static class MouseWheelEvent extends MouseEvent_ {
        double xOffset;
        double yOffset;

        public MouseWheelEvent(Node node, double xOffset, double yOffset) {
            super(node);
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
}
