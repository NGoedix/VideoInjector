package ngoedix.videoinjector.api.eventbus;

import ngoedix.videoinjector.api.eventbus.event.Event;

/**
 * Base class of all default EventSystem Exceptions.
 *
 * @since 1.0.0
 */

public class EventException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public EventException(String msg) {
        super(msg);
    }

    /**
     * Thrown upon problems at event registration.
     *
     * @see VideoInjectorEventBus#registerEvent(Object)
     * @since 1.0.0
     */
    public static class EventRegistryException extends EventException {
        public EventRegistryException(String msg) {
            super(msg);
        }
    }

    /**
     * Thrown when trying to register a SUPREME-Priority.
     *
     * @since 1.0.0
     */
    public static class UnauthorizedRegistryException extends EventException {
        public UnauthorizedRegistryException(String msg) {
            super(msg);
        }
    }

    /**
     * Thrown when trying to cancel a non-cancelable event.
     *
     * @see Event#isCanceled()
     * @since 1.0.0
     */
    public static class EventCancellationException extends EventException {
        public EventCancellationException(String msg) {
            super(msg);
        }
    }
}

