package com.github.unknownstudio.unknowndomain.engineapi.event;

import com.github.unknownstudio.unknowndomain.engineapi.Platform;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.reflections.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventBusUniversal implements EventBus {

    private ConcurrentHashMap<TypeToken<? extends Event>, ArrayList<Pair<EventPriority, Method>>> eventMethodMap;
    private ConcurrentHashMap<TypeToken, ArrayList<Method>> clazzMethodMap;
    private ConcurrentHashMap<Method, TypeToken> methodClazzMap;
    private ConcurrentHashMap<TypeToken, Integer> registeredClazz;
    private ArrayList<Object> registeredObject;

    public EventBusUniversal(){
        eventMethodMap = new ConcurrentHashMap<>();
        clazzMethodMap = new ConcurrentHashMap<>();
        methodClazzMap = new ConcurrentHashMap<>();
        registeredClazz = new ConcurrentHashMap<>();
        registeredObject = new ArrayList<>();
    }

    @Override
    public void post(Event event) {
        TypeToken type = TypeToken.of(event.getClass());
        ArrayList<Pair<EventPriority, Method>> list = eventMethodMap.get(type);
        list.sort((Comparator.comparing(Pair::getLeft)));
        for (Pair<EventPriority, Method> pair : list) {
            registeredObject.stream().filter(obj->methodClazzMap.get(pair.getRight()).getRawType() == obj.getClass()).forEach(obj->
                    {
                        try {
                            pair.getRight().invoke(obj, event);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace(); //TODO: use logger
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
            );
        }
    }

    @Override
    public void register(Object subscriber) {
        TypeToken type = TypeToken.of(subscriber.getClass());
        if(!registeredClazz.contains(type)) {
            for (Method method : ReflectionUtils.getAllMethods(subscriber.getClass(), ReflectionUtils.withAnnotation(Subscribe.class))) {
                if (method.getParameterCount() != 1){
                    Platform.getLogger().warn("cannot register method {}({}) as an event handler. (parameter count mismatch. required: 1 found: {})", method.getName(), Arrays.deepToString(method.getTypeParameters()), method.getParameterCount());
                }
                else{
                    Class<?> tmp = method.getParameterTypes()[0];
                    if (!Event.class.isAssignableFrom(tmp)){
                        Platform.getLogger().warn("cannot register method {}({}) as an event handler. (parameter type mismatch. required subclass of Event.)", method.getName(), Arrays.deepToString(method.getTypeParameters()));
                    }
                    else{
                        TypeToken event = TypeToken.of(tmp.asSubclass(Event.class));
                        EventPriority priority = method.getAnnotation(Subscribe.class).priority();
                        if(!eventMethodMap.containsKey(event))
                            eventMethodMap.put(event, new ArrayList<>());
                        eventMethodMap.get(event).add(new ImmutablePair<>(priority, method));
                        if(!clazzMethodMap.containsKey(type))
                            clazzMethodMap.put(type, new ArrayList<>());
                        clazzMethodMap.get(type).add(method);
                        methodClazzMap.putIfAbsent(method, type);
                    }
                }
            }
            registeredClazz.putIfAbsent(type, 0);
        }
        registeredClazz.replace(type, registeredClazz.get(type) + 1);
        registeredObject.add(subscriber);
    }

    @Override
    public void unregister(Object subscriber) {
        registeredObject.remove(subscriber);
        TypeToken type = TypeToken.of(subscriber.getClass());
        registeredClazz.replace(type, registeredClazz.get(type) - 1);
        if(registeredClazz.get(type) <= 0){
            for (Method method: clazzMethodMap.get(type)) {
                search:
                for (ArrayList<Pair<EventPriority, Method>> value: eventMethodMap.values()) {
                    for (Pair<EventPriority, Method> pair: new ArrayList<>(value)) {
                        if (pair.getRight() == method){
                            value.remove(pair);
                            break search;
                        }
                    }
                }
            }
            clazzMethodMap.remove(type);
            registeredClazz.remove(type);
        }
    }
}
