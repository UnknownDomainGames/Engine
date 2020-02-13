package engine.graphics.vulkan;

import engine.graphics.GraphicsEngine;
import engine.graphics.vulkan.device.PhysicalDevice;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static engine.graphics.vulkan.util.VulkanUtils.translateVulkanResult;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.vkCreateInstance;

public class VulkanInstance {

    private VkInstance instance;

    private boolean released = false;

    private VulkanInstance(){}

    public static List<String> getSupportedExtensionProperties(@Nullable String layerName){
        try(var stack = MemoryStack.stackPush()){
            var size = stack.mallocInt(1);
            var err = VK10.vkEnumerateInstanceExtensionProperties(layerName, size, null);
            if(err != VK_SUCCESS){
                return new ArrayList<>();
            }
            var ptrs = VkExtensionProperties.callocStack(size.get(0), stack);
            err = VK10.vkEnumerateInstanceExtensionProperties(layerName, size, ptrs);
            if(err != VK_SUCCESS){
                return new ArrayList<>();
            }
            return ptrs.stream().map(vkExtensionProperties -> String.format("%s:%d", vkExtensionProperties.extensionNameString(),vkExtensionProperties.specVersion())).collect(Collectors.toList());
        }
    }

    public static VulkanInstance createInstance(@Nullable PointerBuffer extensions, String appName, int appVersion, VulkanVersion apiVersion){
        return createInstance(extensions, null, appName, appVersion, "", 1, apiVersion);
    }

    public static VulkanInstance createInstance(@Nullable PointerBuffer extensions, @Nullable PointerBuffer layers, String appName, int appVersion, VulkanVersion apiVersion){
        return createInstance(extensions, layers, appName, appVersion, "", 1, apiVersion);
    }

    public static VulkanInstance createInstance(@Nullable PointerBuffer extensions, @Nullable PointerBuffer layers, String appName, int appVersion, String engineName, int engineVersion, VulkanVersion apiVersion){
        try(var stack = MemoryStack.stackPush()){
            var appInfo = VkApplicationInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                    .pApplicationName(MemoryStack.stackUTF8(appName))
                    .applicationVersion(appVersion)
                    .pEngineName(MemoryStack.stackUTF8(engineName))
                    .engineVersion(engineVersion)
                    .apiVersion(apiVersion.vk);
            if(extensions == null) extensions = stack.mallocPointer(0);
            PointerBuffer ppEnabledExtensionNames;
            if(GraphicsEngine.isDebug()) {
                ppEnabledExtensionNames = stack.mallocPointer(extensions.remaining() + 1);
                ppEnabledExtensionNames.put(extensions);
                var VK_EXT_DEBUG_REPORT_EXTENSION = MemoryStack.stackUTF8(EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME);
                ppEnabledExtensionNames.put(VK_EXT_DEBUG_REPORT_EXTENSION);
                ppEnabledExtensionNames.flip();
            }
            else{
                ppEnabledExtensionNames = extensions;
            }
            VkInstanceCreateInfo pCreateInfo = VkInstanceCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                    .pNext(NULL)
                    .pApplicationInfo(appInfo)
                    .ppEnabledExtensionNames(ppEnabledExtensionNames)
                    .ppEnabledLayerNames(layers);
            PointerBuffer pInstance = stack.mallocPointer(1);
            int err = vkCreateInstance(pCreateInfo, null, pInstance);
            long instance = pInstance.get(0);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to create VkInstance: " + translateVulkanResult(err));
            }
            var jInstance = new VulkanInstance();
            jInstance.instance = new VkInstance(instance, pCreateInfo);
            return jInstance;
        }
    }

    public VkInstance getNativeInstance() {
        return instance;
    }

    private List<PhysicalDevice> physicalDevices;

    public List<PhysicalDevice> getPhysicalDevices(){
        if(released) throw new IllegalStateException("Instance already destroyed!");
        if(physicalDevices == null) {
            try(var stack = MemoryStack.stackPush()){
                var size = stack.mallocInt(1);
                var err = vkEnumeratePhysicalDevices(instance, size, null);
                if (err != VK_SUCCESS) {
                    throw new IllegalStateException("Failed to get number of physical devices: " + translateVulkanResult(err));
                }
                var ptrs = stack.mallocPointer(size.get(0));
                err = vkEnumeratePhysicalDevices(instance, size, ptrs);
                if (err != VK_SUCCESS) {
                    throw new IllegalStateException("Failed to get physical devices: " + translateVulkanResult(err));
                }
                physicalDevices = new ArrayList<>();
                for (int i = 0; i < size.get(0); i++) {
                    physicalDevices.add(new PhysicalDevice(new VkPhysicalDevice(ptrs.get(i), instance)));
                }
            }
        }
        return physicalDevices;
    }

    public void free(){
        VK10.vkDestroyInstance(instance, null);
        instance = null;
        released = true;
    }
}
