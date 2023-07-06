package org.example.opencv;

public class OpenCVLibFilter implements Unzip.Filter {

    @Override
    public String doFilter(String fileName) {
        final boolean is_windows_x64_file = Platform.isWindows64()
            && fileName.matches(".+64\\.dll");

        final boolean is_windows_x32_file = Platform.isWindows32()
            && fileName.matches(".+x86\\.dll");

        final boolean is_linux_file = Platform.isLinux()
            && fileName.matches(".+\\.so+");

        if (is_windows_x32_file || is_windows_x64_file) {
            return "opencv_java.dll";
        }
        if (is_linux_file) {
            return "libopencv_java.so";
        }

        return null;
    }

}
