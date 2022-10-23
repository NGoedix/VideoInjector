package ngoedix.videoinjector.platform;

import ngoedix.videoinjector.Constants;
import ngoedix.videoinjector.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> clazz) {

        final ServiceLoader<T> loadedService = ServiceLoader.load(clazz);

        final T service = loadedService.iterator().hasNext() ? loadedService.iterator().next() : null;
        if (service == null) {
            throw new NullPointerException("Failed to load service for " + clazz.getName());
        }
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return service;
    }

}
