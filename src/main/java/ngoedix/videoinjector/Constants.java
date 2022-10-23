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
 * along with VideoInjector.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2022 NGoedix.
 */

package ngoedix.videoinjector;

import ngoedix.videoinjector.api.DynamicResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Constants {
    //DLL Version
    public static final int DLL_VERSION = 3;
    public static final String PLUGINSDIR = "plugins/";

    // Mod info
    public static final String MOD_ID = "videoinjector";
    public static final String MOD_NAME = "VideoInjector";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);

    // System info
    public static String OS;
    public static String ARCH;

    // First render Tick
    public static boolean renderTick;

    // String constants
    public static final String AUDIO_OUTPUT = "audio_output";
    public static final String VIDEO_FILTER = "video_filter";
    public static final String ACCESS = "access";

    // No_Library_Mode
    public static boolean NO_LIBRARY_MODE;
    public static final boolean DEBUG_NO_LIBRARY_MODE = false;

    // Empty DynamicResourceLocation;
    public static final DynamicResourceLocation EMPTY_RES_LOC = new DynamicResourceLocation("", "");
}
