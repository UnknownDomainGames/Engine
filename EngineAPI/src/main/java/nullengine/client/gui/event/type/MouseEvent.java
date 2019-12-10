package nullengine.client.gui.event.type;

import nullengine.client.gui.Node;
import nullengine.client.gui.event.EventType;
import nullengine.client.input.keybinding.ActionMode;
import nullengine.client.input.keybinding.Key;

public class MouseEvent extends ComponentEvent {
    public static final EventType<MouseEvent> TYPE = new EventType<>("MouseEvent", ComponentEvent.TYPE);

    public MouseEvent(Node source) {
        this(TYPE, source);
    }

    public MouseEvent(EventType<? extends MouseEvent> type, Node source) {
        super(type, source);
    }

    public static class MousePositionEvent extends MouseEvent {
        public static final EventType<MousePositionEvent> TYPE = new EventType<>("MousePositionEvent", MouseEvent.TYPE);

        public MousePositionEvent(Node source, double oldPosX, double oldPosY, double newPosX, double newPosY) {
            this(TYPE, source, oldPosX, oldPosY, newPosX, newPosY);
        }

        public MousePositionEvent(EventType<? extends MouseEvent> type, Node source, double oldPosX, double oldPosY, double newPosX, double newPosY) {
            super(type, source);
            this.oldPosX = oldPosX;
            this.oldPosY = oldPosY;

            this.newPosX = newPosX;
            this.newPosY = newPosY;
        }

        private double oldPosX;
        private double oldPosY;
        private double newPosX;
        private double newPosY;


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
        public static final EventType<MousePositionEvent> TYPE = new EventType<>("MousePositionEvent", MousePositionEvent.TYPE);

        public MouseMoveEvent(Node node, double oldPosX, double oldPosY, double newPosX, double newPosY) {
            super(TYPE, node, oldPosX, oldPosY, newPosX, newPosY);
        }
    }

    public static class MouseEnterEvent extends MousePositionEvent {
        public static final EventType<MouseEnterEvent> TYPE = new EventType<>("MouseEnterEvent", MousePositionEvent.TYPE);

        public MouseEnterEvent(Node node, double oldPosX, double oldPosY, double newPosX, double newPosY) {
            super(TYPE, node, oldPosX, oldPosY, newPosX, newPosY);
        }
    }

    public static class MouseLeaveEvent extends MousePositionEvent {
        public static final EventType<MouseLeaveEvent> TYPE = new EventType<>("MouseLeaveEvent", MousePositionEvent.TYPE);

        public MouseLeaveEvent(Node node, double oldPosX, double oldPosY, double newPosX, double newPosY) {
            super(TYPE, node, oldPosX, oldPosY, newPosX, newPosY);
        }
    }

    public static class MouseButtonEvent extends MouseEvent {
        public static final EventType<MouseButtonEvent> TYPE = new EventType<>("MouseButtonEvent", MouseEvent.TYPE);

        private Key key;
        private ActionMode mode;
        private float posX;
        private float posY;

        public MouseButtonEvent(EventType<? extends MouseButtonEvent> type, Node node, float posX, float posY, Key key, ActionMode mode) {
            super(type, node);
            this.key = key;
            this.mode = mode;
            this.posX = posX;
            this.posY = posY;
        }

        public MouseButtonEvent(Node node, float posX, float posY, Key key, ActionMode mode) {
            this(TYPE, node, posX, posY, key, mode);
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
        public static final EventType<MouseClickEvent> TYPE = new EventType<>("MouseClickEvent", MouseButtonEvent.TYPE);

        public MouseClickEvent(Node node, float posX, float posY, Key key) {
            super(TYPE, node, posX, posY, key, ActionMode.PRESS);
        }
    }

    public static class MouseReleasedEvent extends MouseButtonEvent {
        public static final EventType<MouseReleasedEvent> TYPE = new EventType<>("MouseReleasedEvent", MouseEvent.MouseButtonEvent.TYPE);

        public MouseReleasedEvent(Node node, float posX, float posY, Key key) {
            super(TYPE, node, posX, posY, key, ActionMode.PRESS);
        }
    }

    public static class MouseHoldEvent extends MouseButtonEvent {
        public static final EventType<MouseHoldEvent> TYPE = new EventType<>("MouseHoldEvent", MouseEvent.MouseButtonEvent.TYPE);

        public MouseHoldEvent(Node node, float posX, float posY, Key key) {
            super(TYPE, node, posX, posY, key, ActionMode.PRESS);
        }
    }

    public static class MouseWheelEvent extends MouseEvent {
        public static final EventType<MouseWheelEvent> TYPE = new EventType<>("MouseWheelEvent", MouseEvent.TYPE);

        double xOffset;
        double yOffset;

        public MouseWheelEvent(Node node, double xOffset, double yOffset) {
            super(TYPE, node);
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
}
