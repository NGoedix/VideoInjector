package ngoedix.videoinjector.api.eventbus.event;

import ngoedix.videoinjector.api.DynamicResourceLocation;
import ngoedix.videoinjector.api.MediaPlayerHandler;
import ngoedix.videoinjector.api.eventbus.EventPhase;

/**
 * Base class of all PlayerRegistry Events
 *
 * @since 1.0.0
 */
public class PlayerRegistryEvent {

    /**
     * Private constructor
     *
     * @since 1.0.0
     */
    private PlayerRegistryEvent() {
    }

    /**
     * Main Event for registering a new MediaPlayer. <br>
     * Adding a player later on is always safe.
     *
     * @since 1.0.0
     */
    public static class AddPlayerEvent extends Event {

        @Override
        public boolean isCancelable() {
            return false;
        }

        /**
         * Convenience methode to get the {@link MediaPlayerHandler}.
         *
         * @return The MediaPlayerHandler Instance.
         * @since 1.0.0
         */
        public MediaPlayerHandler handler() {
            return MediaPlayerHandler.getInstance();
        }

    }

    /**
     * This event gets fired when a mediaPlayer is marked to remove. <br>
     * PRE-Phase: canceling is possible, negating the remove mark. <br>
     * EVENT-Phase: mediaPlayer is removed, at least now you <b>MUST</b> set your reference for this player to null. <br>
     * POST-Phase: called after the player was removed. Can be used to re-register a player with the same
     * {@link DynamicResourceLocation}. <br>
     *
     * @see MediaPlayerHandler
     * @since 1.0.0
     */
    @SuppressWarnings("unused")
    public static class RemovePlayerEvent extends PhasedEvent implements PlayerEvent {

        public final DynamicResourceLocation resourceLocation;

        public RemovePlayerEvent(DynamicResourceLocation resourceLocation) {
            this.resourceLocation = resourceLocation;
        }

        /**
         * Convenience methode to get the {@link MediaPlayerHandler}.
         *
         * @return The MediaPlayerHandler Instance.
         * @since 1.0.0
         */
        public MediaPlayerHandler handler() {
            return MediaPlayerHandler.getInstance();
        }

        @Override
        public void afterPhase() {
            // Cleanup the requested player
            if (phase == EventPhase.EVENT) {
                MediaPlayerHandler.getInstance().getMediaPlayer(resourceLocation).cleanup();
            }
        }

        /**
         *
         * @since 1.0.0
         */
        @Override
        public DynamicResourceLocation getPlayer() {
            return resourceLocation;
        }
    }
}
