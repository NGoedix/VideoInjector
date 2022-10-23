package ngoedix.videoinjector.api.eventbus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A methode annotated with this will be considered a valid event listener.
 *
 * @see EventPhase
 * @see EventPriority
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface VideoInjectorEvent {

    EventPriority priority() default EventPriority.NORMAL;

    EventPhase phase() default EventPhase.EVENT;

    /**
     * This specifies the player an event should run for.
     * Note that this does <b>NOT</b> stop others from catching the event.
     *
     * @since 1.0.0
     */
    String player() default "";
}