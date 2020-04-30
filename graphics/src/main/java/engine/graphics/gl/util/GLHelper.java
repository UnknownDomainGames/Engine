package engine.graphics.gl.util;

import engine.graphics.util.DataType;
import engine.graphics.util.DepthCompareMode;
import engine.graphics.util.GPUVendor;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_COMPARE_R_TO_TEXTURE;
import static org.lwjgl.opengl.GL30.GL_HALF_FLOAT;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;

public final class GLHelper {

    private static GLCapabilities capabilities;

    private static boolean GL_ARB_direct_state_access;
    private static boolean GL_EXT_direct_state_access;

    public static GLCapabilities getCapabilities() {
        return capabilities;
    }

    public static void setup(GLCapabilities capabilities, GPUVendor vendor) {
        GLHelper.capabilities = capabilities;

        if (vendor == GPUVendor.INTEL) {
            // Fix intel IGP cannot work perfectly with DSA
            GL_ARB_direct_state_access = false;
            GL_EXT_direct_state_access = false;
        } else {
            GL_ARB_direct_state_access = capabilities.GL_ARB_direct_state_access;
            GL_EXT_direct_state_access = capabilities.GL_EXT_direct_state_access;
        }
    }

    public static boolean isOpenGL45() {
        return capabilities.OpenGL45;
    }

    public static boolean isOpenGL44() {
        return capabilities.OpenGL44;
    }

    public static boolean isOpenGL43() {
        return capabilities.OpenGL43;
    }

    public static boolean isOpenGL42() {
        return capabilities.OpenGL42;
    }

    public static boolean isOpenGL41() {
        return capabilities.OpenGL41;
    }

    public static boolean isOpenGL40() {
        return capabilities.OpenGL40;
    }

    public static boolean isSupportARBDirectStateAccess() {
        return GL_ARB_direct_state_access;
    }

    public static boolean isSupportARBTextureStorage() {
        return capabilities.GL_ARB_texture_storage;
    }

    public static boolean isSupportEXTDirectStateAccess() {
        return GL_EXT_direct_state_access;
    }

    public static String getVendor() {
        return GL11.glGetString(GL11.GL_VENDOR);
    }

    public static String getRenderer() {
        return GL11.glGetString(GL11.GL_RENDERER);
    }

    public static String getVersion() {
        return GL11.glGetString(GL11.GL_VERSION);
    }

    public static String getExtensions() {
        return GL11.glGetString(GL11.GL_EXTENSIONS);
    }

    public static String getShadingLanguageVersion() {
        return GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);
    }

    public static int getMask(boolean color, boolean depth, boolean stencil) {
        int mask = 0;
        if (color) mask |= GL_COLOR_BUFFER_BIT;
        if (depth) mask |= GL_DEPTH_BUFFER_BIT;
        if (stencil) mask |= GL_STENCIL_BUFFER_BIT;
        return mask;
    }

    public static int toGLDataType(DataType type) {
        switch (type) {
            case BYTE:
                return GL_BYTE;
            case UNSIGNED_BYTE:
                return GL_UNSIGNED_BYTE;
            case SHORT:
                return GL_SHORT;
            case UNSIGNED_SHORT:
                return GL_UNSIGNED_SHORT;
            case INT:
                return GL_INT;
            case UNSIGNED_INT:
                return GL_UNSIGNED_INT;
            case FLOAT:
                return GL_FLOAT;
            case DOUBLE:
                return GL_DOUBLE;
            case HALF_FLOAT:
                return GL_HALF_FLOAT;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static String getErrorMessage() {
        return getFriendlyErrorMessage(GL11.glGetError());
    }

    public static String getFriendlyErrorMessage(int error) {
        switch (error) {
            case GL_NO_ERROR:
                return "NO_ERROR";
            case GL_INVALID_ENUM:
                return "INVALID_ENUM";
            case GL_INVALID_VALUE:
                return "INVALID_VALUE";
            case GL_INVALID_OPERATION:
                return "INVALID_OPERATION";
            case GL_STACK_OVERFLOW:
                return "STACK_OVERFLOW";
            case GL_STACK_UNDERFLOW:
                return "STACK_UNDERFLOW";
            case GL_OUT_OF_MEMORY:
                return "OUT_OF_MEMORY";
            case GL_INVALID_FRAMEBUFFER_OPERATION:
                return "INVALID_FRAMEBUFFER_OPERATION";
            default:
                throw new IllegalArgumentException("Unknown error code");
        }
    }

    public static int toGLCompareMode(DepthCompareMode depthCompareMode) {
        return depthCompareMode != DepthCompareMode.NONE ? GL_COMPARE_R_TO_TEXTURE : GL_NONE;
    }

    public static int toGLCompareFunc(DepthCompareMode depthCompareMode) {
        switch (depthCompareMode) {
            case NONE:
                return GL_NONE;
            case LESS_OR_EQUAL:
                return GL_LEQUAL;
            case LESS:
                return GL_LESS;
            case EQUAL:
                return GL_EQUAL;
            case NEVER:
                return GL_NEVER;
            case ALWAYS:
                return GL_ALWAYS;
            case GREATER:
                return GL_GREATER;
            case NOT_EQUAL:
                return GL_NOTEQUAL;
            case GREATER_OR_EQUAL:
                return GL_GEQUAL;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static void setDebugMessageCallback(DebugMessageCallback callback) throws UnsupportedOperationException {
        if (capabilities.OpenGL43) {
            GL43.glDebugMessageCallback(callback::invoke, 0);
        } else if (capabilities.GL_ARB_debug_output) {
            ARBDebugOutput.glDebugMessageCallbackARB(callback::invoke, 0);
        } else {
            throw new UnsupportedOperationException("Unsupported debug message callback.");
        }
    }

    private GLHelper() {
    }
}
