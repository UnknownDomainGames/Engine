package engine.graphics.graph;

import java.util.Map;

public final class FrameContext {
    private final Frame frame;
    private final Map<String, Object> arguments;

    public FrameContext(Frame frame, Map<String, Object> arguments) {
        this.frame = frame;
        this.arguments = arguments;
    }

    public Frame getFrame() {
        return frame;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }
}
