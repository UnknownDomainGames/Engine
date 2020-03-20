package engine.gui.layout;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.Node;
import engine.gui.misc.Insets;
import engine.gui.misc.Pos;

public class StackPane extends Pane {

    private static final String ALIGNMENT = "stackpane-child-alignment";

    public static void setAlignment(Node node, Pos pos) {
        setProperty(node, ALIGNMENT, pos);
    }

    public static Pos getAlignment(Node node) {
        return (Pos) getProperty(node, ALIGNMENT);
    }

    private final MutableObjectValue<Pos> paneAlignment = new SimpleMutableObjectValue<>(Pos.CENTER);

    public StackPane(){
        paneAlignment.addChangeListener((observable, oldValue, newValue) -> needsLayout());
    }

    public MutableObjectValue<Pos> paneAlignment() {
        return paneAlignment;
    }

    public void addToBottom(Node node){
        getChildren().add(0, node);
    }

    public void addToTop(Node node){
        getChildren().add(node);
    }

    public void addBehind(int index, Node node){
        getChildren().add(index, node);
    }

    public void addAtFront(int index, Node node){
        getChildren().add(index + 1, node);
    }

    public void moveToBottom(int pointer){
        getChildren().add(0, getChildren().remove(pointer));
    }

    public void moveToTop(int pointer){
        getChildren().add(getChildren().remove(pointer));
    }

    public void moveBehind(int pointer){
        if(pointer == 0){
            throw new IndexOutOfBoundsException(String.format("reached the bottom element! pointer: %d", pointer));
        }
        getChildren().add(pointer - 1, getChildren().remove(pointer));
    }

    public void moveAhead(int pointer){
        if(pointer == getChildren().size() - 1){
            throw new IndexOutOfBoundsException(String.format("reached the top element! pointer: %d", pointer));
        }
        getChildren().add(pointer + 1, getChildren().remove(pointer));
    }

    public void moveTo(int pointer, int newIndex){
        if(newIndex < 0 || newIndex >= getChildren().size()){
            throw new IndexOutOfBoundsException(String.format("index out of bound! size: %d found: %d", getChildren().size(), newIndex));
        }
        getChildren().add(newIndex, getChildren().remove(pointer));
    }

    @Override
    protected void layoutChildren() {
        final Insets padding = getPadding();
        float width = getWidth();
        float height = getHeight();
        width = width < minWidth() ? minWidth() : width;
        height = height < minHeight() ? minHeight() : height;


        final float insideX = padding.getLeft();
        final float insideY = padding.getTop();
        final float insideWidth = width - insideX - padding.getRight();
        final float insideHeight = height - insideY - padding.getBottom();
        for (Node child : getChildren()) {
            var childAlignment = getAlignment(child);
            layoutInArea(child, insideX, insideY, insideWidth, insideHeight, 0, Insets.EMPTY,
                    childAlignment != null ? childAlignment.getHpos() : paneAlignment().get().getHpos(),
                    childAlignment != null ? childAlignment.getVpos() : paneAlignment().get().getVpos());
        }
    }
}
