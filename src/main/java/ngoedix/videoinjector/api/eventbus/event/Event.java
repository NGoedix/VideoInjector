package ngoedix.videoinjector.api.eventbus.event;

import ngoedix.videoinjector.Constants;
import ngoedix.videoinjector.api.eventbus.EventException;

/**
 * Base class of all events, extend this if you want an event without phases.
 *
 * @see PhasedEvent
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class Event {
    private boolean canceled = false;

    /**
     * Template Methode. <br>
     * Override this if your event should be cancelable.
     *
     * @see #setCanceled()
     */
    public abstract boolean isCancelable();

    /**
     * All Events having Phases should be a subclass of {@link PhasedEvent}
     *
     * @return False, as this doesn't have phases
     */
    public boolean hasPhases() {
        return false;
    }

    /**
     * Calling this will cancel the event.
     * Events that are cancelled aren't send to lower priority listeners nor get later phases fired. <br>
     * Trying to cancel a non-cancelable event will throw an {@link EventException.EventCancellationException}.
     */
    public void setCanceled() {
        try {
            if (!this.isCancelable()) {
                Constants.LOG.error("################# ERROR #################");
                Constants.LOG.error("Tried to cancel non-cancelable event: {}", this.getClass().getName());
                Constants.LOG.error("#########################################");
                throw new EventException.EventCancellationException("Event not cancelable: " + this.getClass().getName());
            } else {
                this.canceled = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return True if the event is canceled, false otherwise.
     */
    public boolean isCanceled() {
        return this.canceled;
    }

    /**
     * Template Methode. <br>
     * Override this if you need to do something after your event has run.
     */
    public void onFinished() {
        // Template Methode
    }
}
