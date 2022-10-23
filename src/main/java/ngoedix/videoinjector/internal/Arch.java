package ngoedix.videoinjector.internal;

import com.sun.jna.Platform;

public enum Arch {
    x86,
    amd64,
    arm,
    arm64,
    strange,
    unknown;

    private static Arch arch;

    public static Arch getArch() {
        if (arch == null) {
            switch (Platform.ARCH) {
                case ("x86"): arch = x86; break;
                case ("amd64"):
                case  ("x86-64"):arch = amd64; break;
                case ("ppc"):
                case ("ppc64"):
                case("ppc64le"):
                case ("ia64"):
                case ("sparcv9"):
                case ("mips64"):
                case ("mips64el"): arch = strange; break;
                case ("arm"):
                case ("armel"): arch = arm; break;
                case ("arm64"): arch = arm64; break;
                default:  arch = unknown;
            }
        }
        return arch;
    }
}
