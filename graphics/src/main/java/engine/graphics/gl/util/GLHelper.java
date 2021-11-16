package engine.graphics.gl.util;

import engine.graphics.texture.FilterMode;
import engine.graphics.texture.WrapMode;
import engine.graphics.util.DataType;
import engine.graphics.util.DepthCompareMode;
import engine.graphics.util.GPUVendor;
import org.lwjgl.opengl.*;

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
        return GL11C.glGetString(GL11C.GL_VENDOR);
    }

    public static String getRenderer() {
        return GL11C.glGetString(GL11C.GL_RENDERER);
    }

    public static String getVersion() {
        return GL11C.glGetString(GL11C.GL_VERSION);
    }

    public static String getExtensions() {
        return GL11C.glGetString(GL11C.GL_EXTENSIONS);
    }

    public static String getShadingLanguageVersion() {
        return GL11C.glGetString(GL20C.GL_SHADING_LANGUAGE_VERSION);
    }

    public static int getMask(boolean color, boolean depth, boolean stencil) {
        int mask = 0;
        if (color) mask |= GL11C.GL_COLOR_BUFFER_BIT;
        if (depth) mask |= GL11C.GL_DEPTH_BUFFER_BIT;
        if (stencil) mask |= GL11C.GL_STENCIL_BUFFER_BIT;
        return mask;
    }

    public static int toGLDataType(DataType type) {
        switch (type) {
            case BYTE:
                return GL11C.GL_BYTE;
            case UNSIGNED_BYTE:
                return GL11C.GL_UNSIGNED_BYTE;
            case SHORT:
                return GL11C.GL_SHORT;
            case UNSIGNED_SHORT:
                return GL11C.GL_UNSIGNED_SHORT;
            case INT:
                return GL11C.GL_INT;
            case UNSIGNED_INT:
                return GL11C.GL_UNSIGNED_INT;
            case FLOAT:
                return GL11C.GL_FLOAT;
            case DOUBLE:
                return GL11C.GL_DOUBLE;
            case HALF_FLOAT:
                return GL30C.GL_HALF_FLOAT;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static String getErrorMessage() {
        return getFriendlyErrorMessage(GL11C.glGetError());
    }

    public static String getFriendlyErrorMessage(int error) {
        switch (error) {
            case GL11C.GL_NO_ERROR:
                return "NO_ERROR";
            case GL11C.GL_INVALID_ENUM:
                return "INVALID_ENUM";
            case GL11C.GL_INVALID_VALUE:
                return "INVALID_VALUE";
            case GL11C.GL_INVALID_OPERATION:
                return "INVALID_OPERATION";
            case GL11C.GL_STACK_OVERFLOW:
                return "STACK_OVERFLOW";
            case GL11C.GL_STACK_UNDERFLOW:
                return "STACK_UNDERFLOW";
            case GL11C.GL_OUT_OF_MEMORY:
                return "OUT_OF_MEMORY";
            case GL30C.GL_INVALID_FRAMEBUFFER_OPERATION:
                return "INVALID_FRAMEBUFFER_OPERATION";
            default:
                throw new IllegalArgumentException("Unknown error code");
        }
    }

    public static int toGLCompareMode(DepthCompareMode depthCompareMode) {
        return depthCompareMode != DepthCompareMode.NONE ? GL30C.GL_COMPARE_REF_TO_TEXTURE : GL11C.GL_NONE;
    }

    public static int toGLCompareFunc(DepthCompareMode depthCompareMode) {
        switch (depthCompareMode) {
            case NONE:
            case ALWAYS:
                return GL11C.GL_ALWAYS;
            case LESS_OR_EQUAL:
                return GL11C.GL_LEQUAL;
            case LESS:
                return GL11C.GL_LESS;
            case EQUAL:
                return GL11C.GL_EQUAL;
            case NEVER:
                return GL11C.GL_NEVER;
            case GREATER:
                return GL11C.GL_GREATER;
            case NOT_EQUAL:
                return GL11C.GL_NOTEQUAL;
            case GREATER_OR_EQUAL:
                return GL11C.GL_GEQUAL;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static int toGLFilterMode(FilterMode filterMode) {
        switch (filterMode) {
            case LINEAR:
                return GL11C.GL_LINEAR;
            case NEAREST:
                return GL11C.GL_NEAREST;
            case LINEAR_MIPMAP_LINEAR:
                return GL11C.GL_LINEAR_MIPMAP_LINEAR;
            case LINEAR_MIPMAP_NEAREST:
                return GL11C.GL_LINEAR_MIPMAP_NEAREST;
            case NEAREST_MIPMAP_LINEAR:
                return GL11C.GL_NEAREST_MIPMAP_LINEAR;
            case NEAREST_MIPMAP_NEAREST:
                return GL11C.GL_NEAREST_MIPMAP_NEAREST;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static int toGLWrapMode(WrapMode wrapMode) {
        switch (wrapMode) {
            case REPEAT:
                return GL11C.GL_REPEAT;
            case CLAMP_TO_EDGE:
                return GL12C.GL_CLAMP_TO_EDGE;
            case CLAMP_TO_BORDER:
                return GL13C.GL_CLAMP_TO_BORDER;
            case MIRRORED_REPEAT:
                return GL14C.GL_MIRRORED_REPEAT;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static void setDebugMessageCallback(DebugMessageCallback callback) throws UnsupportedOperationException {
        if (capabilities.OpenGL43) {
            GL11C.glEnable(GL43C.GL_DEBUG_OUTPUT);
            GL43C.glDebugMessageCallback(callback::invoke, 0);
            GL43C.glDebugMessageControl(DebugMessageCallback.Source.API.gl, DebugMessageCallback.Type.ERROR.gl, DebugMessageCallback.Severity.HIGH.gl, (int[]) null, true);
        } else if (capabilities.GL_KHR_debug) {
            GL11C.glEnable(KHRDebug.GL_DEBUG_OUTPUT);
            KHRDebug.glDebugMessageCallback(callback::invoke, 0);
            KHRDebug.glDebugMessageControl(DebugMessageCallback.Source.API.gl, DebugMessageCallback.Type.ERROR.gl, DebugMessageCallback.Severity.HIGH.gl, (int[]) null, true);
        } else if (capabilities.GL_ARB_debug_output) {
            ARBDebugOutput.glDebugMessageCallbackARB(callback::invoke, 0);
            ARBDebugOutput.glDebugMessageControlARB(ARBDebugOutput.GL_DEBUG_SOURCE_API_ARB, ARBDebugOutput.GL_DEBUG_TYPE_ERROR_ARB, ARBDebugOutput.GL_DEBUG_SEVERITY_HIGH_ARB, (int[]) null, true);
        } else {
            throw new UnsupportedOperationException("Unsupported debug message callback.");
        }
    }

    private GLHelper() {
    }
}
