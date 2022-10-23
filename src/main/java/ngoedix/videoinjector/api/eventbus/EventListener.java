package ngoedix.videoinjector.api.eventbus;

import ngoedix.videoinjector.api.DynamicResourceLocation;

import java.lang.reflect.Method;

public class EventListener {

    public Method event;
    public Object instance;
    public EventPriority priority;
    public EventPhase phase;
    public DynamicResourceLocation player;

    public EventListener(Method event, Object instance, EventPriority priority, EventPhase phase, DynamicResourceLocation player) {
        this.event = event;
        this.instance = instance;
        this.priority = priority;
        this.phase = phase;
        this.player = player;
    }
}
