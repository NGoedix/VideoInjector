package ngoedix.videoinjector.api.mediaPlayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import ngoedix.videoinjector.Constants;
import ngoedix.videoinjector.api.DynamicResourceLocation;
import ngoedix.videoinjector.api.internal.MediaPlayerCallback;
import ngoedix.videoinjector.api.internal.SelfCleaningDynamicTexture;
import ngoedix.videoinjector.api.internal.utils.IntegerBuffer2D;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * For most custom implementation this should be enough to override.
 *
 * @see AbstractMediaPlayer
 * @since 1.0.0
 */
public class MediaPlayerBase extends AbstractMediaPlayer{
    /**
     * Last available Frame, stored as a {@link DynamicResourceLocation}
     **/
    public final DynamicResourceLocation dynamicResourceLocation;
    // MediaPlayerCallback
    public final MediaPlayerCallback callback = new MediaPlayerCallback(0, this);
    /**
     * Last available Frame, stored as a {@link NativeImage}
     **/
    protected NativeImage image = new NativeImage(1, 1, true); //TODO This redundant initializer seems to be important?
    /**
     * Last available Frame, stored as a {@link SelfCleaningDynamicTexture}
     **/
    protected SelfCleaningDynamicTexture dynamicTexture = new SelfCleaningDynamicTexture(image);

    public MediaPlayerBase(DynamicResourceLocation resourceLocation) {
        image = new NativeImage(1, 1, true);
        image.setPixelRGBA(0, 0, 0);
        dynamicTexture.setPixels(image);
        dynamicResourceLocation = resourceLocation;
        Minecraft.getInstance().getTextureManager().register(resourceLocation.toWorkingString().replace(':', '.'), dynamicTexture);
        Constants.LOG.info("TextureLocation is '{}'", dynamicResourceLocation);
    }

    /**
     * Template methode. <br>
     * Overrides should always return a valid {@link EmbeddedMediaPlayer}
     *
     * @return null
     * @since 1.0.0
     */
    @Override
    public EmbeddedMediaPlayer api() {
        return null;
    }

    @Override
    public void markToRemove() {
        // Template methode.
    }

    @Override
    public void cleanup() {
        // Template methode.
    }

    public int[] getIntFrame() {
        return new int[0];
    }

    /**
     * Template methode. <br>
     * This returns the width of the current video frame.
     *
     * @since 1.0.0
     */
    public int getWidth() {
        return 0; //NOSONAR
    }

    /**
     * Template methode. <br>
     * Invoked by the callback to set a new frame. Should only be used by the callback, or if you want to inject custom frames.
     *
     * @since 1.0.0
     */
    public void setIntBuffer(IntegerBuffer2D in) {
        // Template methode.
    }

    /**
     * Template methode. <br>
     *
     * @since 1.0.0
     */
    public IntegerBuffer2D getIntBuffer() {
        return new IntegerBuffer2D(1, 1);
    }

    /**
     * Template methode. <br>
     * Renders the current frame to a {@link ResourceLocation} for further use.
     *
     * @return The {@link ResourceLocation} rendered to.
     */
    public ResourceLocation renderToResourceLocation() {
        return dynamicResourceLocation;
    }
}
