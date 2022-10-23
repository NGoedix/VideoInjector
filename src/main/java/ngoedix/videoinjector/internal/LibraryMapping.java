package ngoedix.videoinjector.internal;

import ngoedix.videoinjector.Constants;

public enum LibraryMapping {
    // core
    libVLC("libvlc", false),
    libVLCCore("libvlccore", false),

    // audio_filter
    libEqualizer("audio_filter", "libequalizer_plugin"),

    // audio_output
    libADummy(Constants.AUDIO_OUTPUT, "libadummy_plugin"),
    libAMem(Constants.AUDIO_OUTPUT, "libamem_plugin"),
    libDirectSound(Constants.AUDIO_OUTPUT, "libdirectsound_plugin"),

    // logger
    libConsoleLogger("logger", "libconsole_logger_plugin"),
    libFileLogger("logger", "libfile_logger_plugin"),

    // spu
    libLogo("spu", "liblogo_plugin"),
    libMarq("spu", "libmarq_plugin"),

    // video_filter // TODO: Find out if we need all of those
    libAdjust(Constants.VIDEO_FILTER, "libadjust_plugin"),
    libAlphaMask(Constants.VIDEO_FILTER, "libalphamask_plugin"),
    libDeinterlace(Constants.VIDEO_FILTER, "libdeinterlace_plugin"),
    libFPS(Constants.VIDEO_FILTER, "libfps_plugin"),

    // video_output
    libVDummy("video_output", "libwdummy_plugin"),
    libVMem("video_output", "libvmem_plugin"),

    // video_chroma
    //libI420RGB("video_chroma", "libi420_rgb_plugin"),
    libSWScale("video_chroma", "libswscale_plugin"),

    // access
    libFilesystem(Constants.ACCESS, "libfilesystem_plugin"),
    libHttp(Constants.ACCESS, "libhttp_plugin"),
    libHttps(Constants.ACCESS, "libhttps_plugin"),

    // misc
    libTLS("misc", "libgnutls_plugin"),

    // codec
    libAVCodec("codec", "libavcodec_plugin");


    public final String windowsName;
    public final String linuxName;
    public final String macName;
    public final boolean isPlugin;

    LibraryMapping(String windowsName, String linuxName, String macName, boolean isPlugin) {
        this.windowsName = windowsName;
        this.linuxName = linuxName;
        this.macName = macName;
        this.isPlugin = isPlugin;
    }

    LibraryMapping(String windowsName, String linuxName, String macName) {
        this(windowsName, linuxName, macName, true);
    }

    LibraryMapping(String simpleName, boolean isPlugin) {
        this(simpleName + ".dll", simpleName + ".so", simpleName + ".dylib", isPlugin);
    }

    LibraryMapping(String prefix, String simpleName) {
        this(prefix + "/" + simpleName, true);
    }

    LibraryMapping(String simpleName) {
        this(simpleName, true);
    }

    public String getByOS(String os) {
        String result;
        switch (os) {
            case "windows": result = windowsName; break;
            case "mac": result = macName; break;
            case "linux": result = linuxName; break;
            default: throw new UnsupportedOperationException("Invalid OS");
        }

        return result;
    }
}
