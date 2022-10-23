package ngoedix.videoinjector.api.mediaPlayer;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * Abstract MediaPlayer. This is the minimal starting point for own implementations.
 *
 * @see MediaPlayerBase
 * @since 1.0.0
 */
public abstract class AbstractMediaPlayer {

    /**
     * @return This returns the true {@link EmbeddedMediaPlayer}, allowing you to use most functions of libvlc.
     * This is now the preferred way to call into VLCJ's API. You must be aware that crashes in the native lib will lead to null pointers.
     * @since 1.0.0
     */
    public abstract EmbeddedMediaPlayer api();

    /**
     * Call this to remove (/unregister) your MediaPlayer.
     *
     * @since 1.0.0
     */
    public abstract void markToRemove();

    /**
     * This is forcefully called when the MediaPlayer is removed (/unregistered).
     * Implement custom cleanup behavior by overriding this methode.
     *
     * @since 1.0.0
     */
    public abstract void cleanup();

    /**
     * Template methode. <br>
     * If your Player provides an {@link EmbeddedMediaPlayer} this <b>must</b> return true.
     * @return True if the player provides the EmbeddedMediaPlayer API, false otherwise
     */
    public boolean providesAPI() {
        return false;
    }
}
