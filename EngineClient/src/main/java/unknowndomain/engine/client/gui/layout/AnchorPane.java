package unknowndomain.engine.client.gui.layout;

import unknowndomain.engine.client.gui.Component;

public class AnchorPane extends Pane {
    public static final String TOP_ANCHOR = "anchor_top";
    public static final String BOTTOM_ANCHOR = "anchor_bottom";
    public static final String LEFT_ANCHOR = "anchor_left";
    public static final String RIGHT_ANCHOR = "anchor_right";

    public static void setTopAnchor(Component child, Float value){
        setProperty(child, TOP_ANCHOR, value);
    }
    public static void setBottomAnchor(Component child, Float value){
        setProperty(child, BOTTOM_ANCHOR, value);
    }
    public static void setLeftAnchor(Component child, Float value){
        setProperty(child, LEFT_ANCHOR, value);
    }
    public static void setRightAnchor(Component child, Float value){
        setProperty(child, RIGHT_ANCHOR, value);
    }

    public static Float getTopAnchor(Component child){
        return (Float)getProperty(child, TOP_ANCHOR);
    }
    public static Float getBottomAnchor(Component child){
        return (Float)getProperty(child, BOTTOM_ANCHOR);
    }
    public static Float getLeftAnchor(Component child){
        return (Float)getProperty(child, LEFT_ANCHOR);
    }
    public static Float getRightAnchor(Component child){
        return (Float)getProperty(child, RIGHT_ANCHOR);
    }

    private float computeChildWidth(Component component, Float left, Float right, float areaWidth){
        if(left != null && right != null){
            return areaWidth - left - right;
        }
        return component.prefWidth();
    }
    private float computeChildHeight(Component component, Float top, Float bottom, float areaHeight){
        if(top != null && bottom != null){
            return areaHeight - top - bottom;
        }
        return component.prefHeight();
    }

    @Override
    protected void layoutChildren() {
        final var children = getChildren();
        for (Component child : children) {
            var top = getTopAnchor(child);
            var bottom = getBottomAnchor(child);
            var left = getLeftAnchor(child);
            var right = getRightAnchor(child);


            float x = 0,y = 0;
            float w = computeChildWidth(child, left,right,width().get());
            float h = computeChildHeight(child, top,bottom,height().get());
            if(left != null){
                x = left;
            }else if(right != null){
                x = width().get() - right - child.width().get();
            }
            if(top != null){
                y = top;
            }else if(bottom != null){
                y = height().get() - bottom - child.height().get();
            }

            layoutInArea(child, x,y,w,h);
        }
    }
}
