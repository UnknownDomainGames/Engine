package engine.graphics.vulkan;

import engine.graphics.GraphicsEngine;
import engine.graphics.vulkan.device.PhysicalDevice;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static engine.graphics.vulkan.util.VulkanUtils.translateVulkanResult;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.vkCreateInstance;

public class VulkanInstance {

    private VkInstance instance;

    private boolean released = false;

    private VulkanInstance(){}

    private static Map<String, String> supportedLayers = null;

    private static Map<String, List<String>> supportedExtensions = new HashMap<>();

    public static Map<String, String> getSupportedLayerProperties(){
        if(supportedLayers == null) {
            try(var stack = MemoryStack.stackPush()){
                var size = stack.mallocInt(1);
                var err = VK10.vkEnumerateInstanceLayerProperties(size, null);
                if(err != VK_SUCCESS){
                    supportedLayers = new HashMap<>();
                }
                var ptrs = VkLayerProperties.callocStack(size.get(0), stack);
                err = VK10.vkEnumerateInstanceLayerProperties(size, ptrs);
                if(err != VK_SUCCESS) {
                    supportedLayers = new HashMap<>();
                }
                supportedLayers = ptrs.stream().collect(Collectors.toMap(VkLayerProperties::layerNameString, VkLayerProperties::descriptionString));
            }
        }
        return supportedLayers;
    }

    public static List<String> getSupportedExtensionProperties(@Nullable String layerName){
        if(layerName != null && !isLayerSupported(layerName)) return new ArrayList<>();
        return supportedExtensions.computeIfAbsent(layerName != null ? layerName : "", key -> {
            try (var stack = MemoryStack.stackPush()) {
                var size = stack.mallocInt(1);
                var err = VK10.vkEnumerateInstanceExtensionProperties(layerName, size, null);
                if (err != VK_SUCCESS) {
                    return new ArrayList<>();
                }
                var ptrs = VkExtensionProperties.callocStack(size.get(0), stack);
                err = VK10.vkEnumerateInstanceExtensionProperties(layerName, size, ptrs);
                if (err != VK_SUCCESS) {
                    return new ArrayList<>();
                }
                return ptrs.stream().map(VkExtensionProperties::extensionNameString).collect(Collectors.toList());
            }
        });
    }

    public static boolean isLayerSupported(String layer){
        return getSupportedLayerProperties().containsKey(layer);
    }

    public static boolean isExtensionSupported(String extension, @Nullable String layerName) {
        return getSupportedExtensionProperties(layerName).contains(extension);
    }

    public static VulkanInstance createInstance(@Nullable List<String> extensions, String appName, int appVersion, VulkanVersion apiVersion){
        return createInstance(extensions, null, appName, appVersion, "", 1, apiVersion);
    }

    public static VulkanInstance createInstance(@Nullable List<String> extensions, @Nullable List<String> layers, String appName, int appVersion, VulkanVersion apiVersion){
        return createInstance(extensions, layers, appName, appVersion, "", 1, apiVersion);
    }

    public static VulkanInstance createInstance(@Nullable List<String> extensions, @Nullable List<String> layers, String appName, int appVersion, String engineName, int engineVersion, VulkanVersion apiVersion){
        try(var stack = MemoryStack.stackPush()){
            var appInfo = VkApplicationInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                    .pApplicationName(MemoryStack.stackUTF8(appName))
                    .applicationVersion(appVersion)
                    .pEngineName(MemoryStack.stackUTF8(engineName))
                    .engineVersion(engineVersion)
                    .apiVersion(apiVersion.vk);
            VkInstanceCreateInfo pCreateInfo = VkInstanceCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                    .pApplicationInfo(appInfo);
            if(layers != null){
                var ppLayers = stack.mallocPointer(layers.size());

                var iterator = layers.iterator();
                while (iterator.hasNext()) {
                    String layer = iterator.next();
                    if (!isLayerSupported(layer)) {
                        iterator.remove();
                    }
                    else {
                        var p = MemoryStack.stackUTF8(layer);
                        ppLayers.put(p);
                    }
                }
                ppLayers.flip();
                pCreateInfo.ppEnabledLayerNames(ppLayers);
            }
            if(extensions != null) {
                var ppEnabledExtensionNames = stack.mallocPointer(extensions.size());
                Predicate<String> predicate = o -> {
                    boolean flag = false;
                    if(layers != null) {
                        flag = layers.stream().anyMatch(layer -> isExtensionSupported(o, layer));
                    }
                    return flag || isExtensionSupported(o, null);
                };
                for (String extension : extensions) {
                    if(!predicate.test(extension)) continue;
                    var p = MemoryStack.stackUTF8(extension);
                    ppEnabledExtensionNames.put(p);
                }
                ppEnabledExtensionNames.flip();
                pCreateInfo.ppEnabledExtensionNames(ppEnabledExtensionNames);
            }
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
