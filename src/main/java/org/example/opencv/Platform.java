package org.example.opencv;

import org.apache.commons.lang3.ArchUtils;
import org.apache.commons.lang3.SystemUtils;

public enum Platform {

    WINDOWS_32, WINDOWS_64, LINUX;

    private static final boolean IS_64 = ArchUtils.getProcessor().is64Bit();

    public static boolean isWindows64() {
        return get() == WINDOWS_64;
    }

    public static boolean isWindows32() {
        return get() == WINDOWS_32;
    }

    public static boolean isLinux() {
        return get() == LINUX;
    }

    public static Platform get() {
        if (SystemUtils.IS_OS_WINDOWS && IS_64) {
            return WINDOWS_64;
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            return WINDOWS_32;
        }
        if (SystemUtils.IS_OS_LINUX) {
            return LINUX;
        }

        return null;
    }

}
