/*
 * This file is part of the VideoInjector.
 *
 * The VideoInjector is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The VideoInjector is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * The VideoInjector uses VLCJ, Copyright 2009-2021 Caprica Software Limited,
 * licensed under the GNU General Public License.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You should have received a copy of the GNU General Public License
 * along with VideoInjector. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2022 NGoedix.
 */

package ngoedix.videoinjector;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.EventSubclassTransformer;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import ngoedix.videoinjector.api.eventbus.VideoInjectorEventBus;
import ngoedix.videoinjector.config.SimpleConfig;
import ngoedix.videoinjector.example.DrawBackgroundEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;

import java.util.Arrays;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Constants.MOD_ID)
public class VideoInjector {

    // Config Holder
    private SimpleConfig config;

    // Common Class Holder
    private CommonMainClass commonClass;

    public VideoInjector() {
        // Client only
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            Constants.LOG.warn("## WARNING ## 'VideoInjector' is a client mod and has no effect when loaded on a server!");
            return;
        }

        // Init Config
        config = new VideoInjectorConfig();

        // Ignore the silly NullPointers caused by ModLauncher // TODO Make this actually STOP the error
        if (LogManager.getLogger(EventSubclassTransformer.class) instanceof org.apache.logging.log4j.core.Logger && !config.getAsBool("debugLog")) {
            org.apache.logging.log4j.core.Logger logger = (org.apache.logging.log4j.core.Logger) LogManager.getLogger(EventSubclassTransformer.class);
            logger.warn("## WARNING ## 'VideoInjector' is modifying this log! Disable this behavior in its config BEFORE reporting bugs!");
            logger.addFilter(new AbstractFilter() {
                @Override
                public Result filter(LogEvent event) {
                    if (event.getMessage() != null && event.getThrown() != null && event.getMarker() != null) {
                        if (event.getMarker().getName().equals("EVENTBUS") && event.getMessage().getFormattedMessage().equals("An error occurred building event handler")) {
                            if (Arrays.stream(event.getThrown().getStackTrace()).anyMatch(sTE -> sTE.getClassName().startsWith("uk.co.caprica.vlcj."))) {
                                return Result.DENY;
                            }
                        }
                    }
                    return Result.NEUTRAL;
                }
            });
        }

        commonClass = new CommonMainClass(config);

        MinecraftForge.EVENT_BUS.addListener(this::firstRenderTick);
        MinecraftForge.EVENT_BUS.addListener(this::drawBackground);
    }

    public void firstRenderTick(TickEvent.RenderTickEvent event) {
        if (!Constants.renderTick) {
            commonClass.apiSetup();
            Constants.renderTick = true;
        }
    }

    public void drawBackground(GuiScreenEvent.BackgroundDrawnEvent e) {
        VideoInjectorEventBus.getInstance().runEvent(new DrawBackgroundEvent(e.getGui(), e.getMatrixStack()));
    }
}
