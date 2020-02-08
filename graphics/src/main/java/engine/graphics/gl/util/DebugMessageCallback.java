package engine.graphics.gl.util;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL43.*;

public abstract class DebugMessageCallback {

    public enum Source {
        API(GL_DEBUG_SOURCE_API),
        WINDOW_SYSTEM(GL_DEBUG_SOURCE_WINDOW_SYSTEM),
        SHADER_COMPILER(GL_DEBUG_SOURCE_SHADER_COMPILER),
        THIRD_PARTY(GL_DEBUG_SOURCE_THIRD_PARTY),
        APPLICATION(GL_DEBUG_SOURCE_APPLICATION),
        OTHER(GL_DEBUG_SOURCE_OTHER);

        public final int gl;

        Source(int gl) {
            this.gl = gl;
        }
    }

    public enum Type {
        ERROR(GL_DEBUG_TYPE_ERROR),
        DEPRECATED_BEHAVIOR(GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR),
        UNDEFINED_BEHAVIOR(GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR),
        PORTABILITY(GL_DEBUG_TYPE_PORTABILITY),
        PERFORMANCE(GL_DEBUG_TYPE_PERFORMANCE),
        OTHER(GL_DEBUG_TYPE_OTHER),
        MARKER(GL_DEBUG_TYPE_MARKER);

        public final int gl;

        Type(int gl) {
            this.gl = gl;
        }
    }

    public enum Severity {
        HIGH(GL_DEBUG_SEVERITY_HIGH),
        MEDIUM(GL_DEBUG_SEVERITY_MEDIUM),
        LOW(GL_DEBUG_SEVERITY_LOW),
        NOTIFICATION(GL_DEBUG_SEVERITY_NOTIFICATION);

        public final int gl;

        Severity(int gl) {
            this.gl = gl;
        }
    }

    public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
        Source enumSource = Source.values()[source - GL_DEBUG_SOURCE_API];
        Type enumType = type == GL_DEBUG_TYPE_MARKER ? Type.MARKER : Type.values()[type - GL_DEBUG_TYPE_ERROR];
        Severity enumSeverity = type == GL_DEBUG_SEVERITY_NOTIFICATION ?
                Severity.NOTIFICATION : Severity.values()[severity - GL_DEBUG_SEVERITY_HIGH];
        String stringMessage = MemoryUtil.memUTF8Safe(message, length);
        invoke(enumSource, enumType, id, enumSeverity, stringMessage, userParam);
    }

    public abstract void invoke(Source source, Type type, int id, Severity severity, String message, long userParam);
}
