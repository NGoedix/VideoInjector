package ngoedix.videoinjector.api.eventbus.event;

import ngoedix.videoinjector.api.DynamicResourceLocation;

public interface PlayerEvent {
    DynamicResourceLocation getPlayer();
}
