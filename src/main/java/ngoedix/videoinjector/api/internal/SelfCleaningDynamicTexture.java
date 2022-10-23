package ngoedix.videoinjector.api.internal;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureUtil;
import ngoedix.videoinjector.Constants;

import javax.annotation.Nonnull;

/**
 * Implementation of a self-cleaning DynamicTexture. Allows for high frame rate uploads.
 *
 * @since 1.0.0
 */
public class SelfCleaningDynamicTexture extends DynamicTexture {

    public SelfCleaningDynamicTexture(NativeImage nativeImage) {
        super(nativeImage);
    }

    @Override
    public void setPixels(@Nonnull NativeImage nativeImage) {
        super.setPixels(nativeImage);
        if (this.getPixels() != null) {
            TextureUtil.prepareImage(this.getId(), this.getPixels().getWidth(), this.getPixels().getHeight());
            this.upload();
        } else {
            Constants.LOG.error("Called setPixels in {} with NativeImage.getPixels == null", this.getClass().getName());
        }
    }
}
