package engine.gui;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import com.github.mouse0w0.observable.value.*;
import engine.gui.internal.GUIPlatform;
import engine.gui.internal.StageHelper;
import engine.graphics.display.Window;
import engine.graphics.display.callback.*;
import engine.graphics.image.ReadOnlyImage;

import java.util.ArrayList;

public class Stage {

    private static final ObservableList<Stage> stages = ObservableCollections.observableList(new ArrayList<>());
    private static final ObservableList<Stage> unmodifiableStages = ObservableCollections.unmodifiableObservableList(stages);

    private MutableIntValue x;
    private MutableIntValue y;

    private MutableIntValue width;
    private MutableIntValue height;

    private final MutableObjectValue<Scene> scene = new SimpleMutableObjectValue<>() {
        @Override
        public void set(Scene value) {
            Scene oldScene = get();
            if (oldScene != null) {
                oldScene.stage.set(null);
            }
            super.set(value);
            if (value != null) {
                value.stage.set(Stage.this);
            }
        }
    };

    private MutableStringValue title;

    private ObservableList<ReadOnlyImage> icons;

    private MutableBooleanValue focused;
    private MutableBooleanValue iconified;
    private MutableBooleanValue maximized;
    private MutableBooleanValue resizable;
    private MutableBooleanValue alwaysOnTop;

    private final MutableBooleanValue showing = new SimpleMutableBooleanValue();

    private Window window;

    private boolean primary;

    static {
        StageHelper.setStageAccessor(new StageHelper.StageAccessor() {
            @Override
            public Window getWindow(Stage stage) {
                return stage.window;
            }

            @Override
            public void setWindow(Stage stage, Window window) {
                stage.window = window;
            }

            @Override
            public boolean isPrimary(Stage stage) {
                return stage.primary;
            }

            @Override
            public void setPrimary(Stage stage, boolean primary) {
                stage.primary = primary;
            }

            @Override
            public MutableBooleanValue getShowingProperty(Stage stage) {
                return stage.showing;
            }

            @Override
            public void doVisibleChanged(Stage stage, boolean value) {
                stage.doVisibleChanged(value);
            }
        });
    }

    public static ObservableList<Stage> getStages() {
        return unmodifiableStages;
    }

    public Stage() {
    }

    private MutableIntValue xImpl() {
        if (x == null) {
            x = new SimpleMutableIntValue();
        }
        return x;
    }

    public final ObservableIntValue x() {
        return xImpl().toUnmodifiable();
    }

    public final int getX() {
        return x == null ? 0 : x.get();
    }

    private MutableIntValue yImpl() {
        if (y == null) {
            y = new SimpleMutableIntValue();
        }
        return y;
    }

    public final ObservableIntValue y() {
        return yImpl().toUnmodifiable();
    }

    public final int getY() {
        return y == null ? 0 : y.get();
    }

    private MutableIntValue widthImpl() {
        if (width == null) {
            width = new SimpleMutableIntValue();
        }
        return width;
    }

    public final ObservableIntValue width() {
        return widthImpl().toUnmodifiable();
    }

    public final int getWidth() {
        return width == null ? 0 : width.get();
    }

    private MutableIntValue heightImpl() {
        if (height == null) {
            height = new SimpleMutableIntValue();
        }
        return height;
    }

    public final ObservableIntValue height() {
        return height.toUnmodifiable();
    }

    public final int getHeight() {
        return height == null ? 0 : height.get();
    }

    public final MutableObjectValue<Scene> scene() {
        return scene;
    }

    public final Scene getScene() {
        return scene.get();
    }

    public final void setScene(Scene scene) {
        this.scene.set(scene);
    }

    public final MutableStringValue title() {
        if (title == null) {
            title = new SimpleMutableStringValue() {
                @Override
                public void set(String value) {
                    super.set(value);
                    if (window != null) {
                        window.setTitle(value);
                    }
                }
            };
        }
        return title;
    }

    public final String getTitle() {
        return title == null ? "" : title.get();
    }

    public final void setTitle(String title) {
        title().set(title);
    }

    public ObservableList<ReadOnlyImage> getIcons() {
        if (icons == null) {
            icons = ObservableCollections.observableList(new ArrayList<>());
            icons.addChangeListener(change -> {
                if (window != null) {
                    window.setIcon(icons.toArray(ReadOnlyImage[]::new));
                }
            });
        }
        return icons;
    }

    private MutableBooleanValue focusedImpl() {
        if (focused == null) {
            focused = new SimpleMutableBooleanValue();
        }
        return focused;
    }

    public final ObservableBooleanValue focused() {
        return focusedImpl().toUnmodifiable();
    }

    public final boolean isFocused() {
        return focused != null && focused.get();
    }

    public final void focus() {
        if (window != null) {
            window.focus();
        }
    }

    private MutableBooleanValue iconifiedImpl() {
        if (iconified == null) {
            iconified = new SimpleMutableBooleanValue();
        }
        return iconified;
    }

    public final ObservableBooleanValue iconified() {
        return iconified.toUnmodifiable();
    }

    public final boolean isIconified() {
        return iconified != null && iconified.get();
    }

    private MutableBooleanValue maximizedImpl() {
        if (maximized == null) {
            maximized = new SimpleMutableBooleanValue();
        }
        return maximized;
    }

    public final ObservableBooleanValue getMaximized() {
        return maximizedImpl().toUnmodifiable();
    }

    public final boolean isMaximized() {
        return maximized != null && maximized.get();
    }

    public final void iconify() {
        iconifiedImpl().set(true);
        if (window != null) {
            window.iconify();
        }
    }

    public final void maximize() {
        maximizedImpl().set(true);
        if (window != null) {
            window.maximize();
        }
    }

    public final void restore() {
        iconifiedImpl().set(false);
        maximizedImpl().set(false);
        if (window != null) {
            window.restore();
        }
    }

    public final MutableBooleanValue resizable() {
        if (resizable == null) {
            resizable = new SimpleMutableBooleanValue(true) {
                @Override
                public void set(boolean value) {
                    super.set(value);
                    if (window != null) {
                        window.setResizable(value);
                    }
                }
            };
        }
        return resizable;
    }

    public final boolean isResizable() {
        return resizable == null || resizable.get();
    }

    public final void setResizable(boolean resizable) {
        resizable().set(resizable);
    }

    public final MutableBooleanValue alwaysOnTop() {
        if (alwaysOnTop == null) {
            alwaysOnTop = new SimpleMutableBooleanValue(true) {
                @Override
                public void set(boolean value) {
                    super.set(value);
                    if (window != null) {
                        window.setFloating(value);
                    }
                }
            };
        }
        return alwaysOnTop;
    }

    public final boolean isAlwaysOnTop() {
        return alwaysOnTop != null && alwaysOnTop.get();
    }

    public final void setAlwaysOnTop(boolean alwaysOnTop) {
        alwaysOnTop().set(alwaysOnTop);
    }

    public final ObservableBooleanValue showing() {
        return showing.toUnmodifiable();
    }

    public final boolean isShowing() {
        return showing.get();
    }

    public void show() {
        GUIPlatform.getInstance().getStageHelper().show(this);
    }

    public void hide() {
        GUIPlatform.getInstance().getStageHelper().hide(this);
    }

    private WindowPosCallback posCallback;
    private WindowSizeCallback sizeCallback;
    private WindowFocusCallback focusCallback;
    private WindowIconifyCallback iconifyCallback;
    private WindowMaximizeCallback maximizeCallback;
    private WindowCloseCallback closeCallback;

    private void doVisibleChanged(boolean value) {
        if (value) {
            window.setTitle(getTitle());
            if (icons != null) {
                window.setIcon(icons.toArray(ReadOnlyImage[]::new));
            }
            window.setResizable(isResizable());
            window.setFloating(isAlwaysOnTop());
            if (isIconified() || isMaximized()) {
                if (isIconified()) window.iconify();
                if (isMaximized()) window.maximize();
            } else {
                window.restore();
            }

            if (posCallback == null) posCallback = (window, x, y) -> {
                xImpl().set(x);
                yImpl().set(y);
            };
            window.addWindowPosCallback(posCallback);
            if (sizeCallback == null) sizeCallback = (window, width, height) -> {
                widthImpl().set(width);
                heightImpl().set(height);
            };
            window.addWindowSizeCallback(sizeCallback);
            if (focusCallback == null) focusCallback = (window, focused) -> focusedImpl().set(focused);
            window.addWindowFocusCallback(focusCallback);
            if (iconifyCallback == null) iconifyCallback = (window, iconified) -> iconifiedImpl().set(iconified);
            window.addWindowIconifyCallback(iconifyCallback);
            if (maximizeCallback == null) maximizeCallback = (window, maximized) -> maximizedImpl().set(maximized);
            window.addWindowMaximizeCallback(maximizeCallback);
            if (closeCallback == null) closeCallback = window -> hide();
            window.addWindowCloseCallback(closeCallback);

            stages.add(this);
        } else {
            stages.remove(this);
            window.removeWindowPosCallback(posCallback);
            window.removeWindowSizeCallback(sizeCallback);
            window.removeWindowFocusCallback(focusCallback);
            window.removeWindowIconifyCallback(iconifyCallback);
            window.removeWindowMaximizeCallback(maximizeCallback);
            window.removeWindowCloseCallback(closeCallback);
        }
    }
}
