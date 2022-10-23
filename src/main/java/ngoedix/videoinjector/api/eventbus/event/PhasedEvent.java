package ngoedix.videoinjector.api.eventbus.event;

import ngoedix.videoinjector.api.eventbus.EventPhase;
import ngoedix.videoinjector.api.eventbus.VideoInjectorEventBus;

/**
 * Base class of all phased events, extend this if you want an event with phases.
 *
 * @see Event
 * @since 1.0.0
 */
public abstract class PhasedEvent extends Event {

    protected EventPhase phase = EventPhase.PRE;

    /**
     * @return True, as long as the phase is not {@link EventPhase}.Post;
     * @since 1.0.0
     */
    @Override
    public boolean isCancelable() {
        return phase != EventPhase.POST;
    }

    /**
     * @return True
     * @since 1.0.0
     */
    @Override
    public boolean hasPhases() {
        return true;
    }

    /**
     * Will call the next phase, as soon as the previous one finished.
     * Override this methode to override this behavior. For other behavior changes use {@link #afterPhase()}.
     *
     * @since 1.0.0
     */
    @Override
    public void onFinished() {
        afterPhase();
        if (!isCanceled() && phase != EventPhase.POST) {
            phase = EventPhase.values()[phase.ordinal()];
            VideoInjectorEventBus.getInstance().fire(this, phase);
        }
    }

    /**
     * Template methode. <br>
     * Meant for simple behavior changes after a phase.
     *
     * @see #onFinished()
     * @since 1.0.0
     */
    public abstract void afterPhase();
}
