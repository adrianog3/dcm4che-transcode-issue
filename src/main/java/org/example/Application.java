package org.example;

import lombok.extern.log4j.Log4j2;
import org.dcm4che3.data.UID;
import org.dcm4che3.imageio.codec.Transcoder;
import org.dcm4che3.io.DicomEncodingOptions;
import org.example.opencv.OpenCVLibFilter;
import org.example.opencv.Unzip;

import javax.imageio.ImageIO;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
public class Application {

    static {
        try {
            Unzip.from("opencv_libs.zip")
                .withFilter(new OpenCVLibFilter())
                .replaceIfExists(true)
                .extractTo("opencv_libs");
        } catch (Exception e) {
            log.error("Failed to extract opencv_lib from resource: ", e);
        }

        ImageIO.scanForPlugins();
    }

    public static void main(String[] args) {
        try {
            String lib = System.getProperty("java.library.path");

            if (!lib.contains("opencv_libs")) {
                throw new IllegalStateException("Please, provide jvm argument: -Djava.library.path=opencv_libs");
            }

            Path transcodeDir = Paths.get("transcode");

            transcode(Paths.get("sample", "1.3.46.670589.11.4140.9.1238909502827899400701.16.2.1.1.0.0.2.dcm"),
                transcodeDir.resolve("1.dcm"), UID.JPEGLSLossless);

            transcode(transcodeDir.resolve("1.dcm"), transcodeDir.resolve("2.dcm"), UID.JPEGLosslessSV1);
        } catch (Exception e) {
            log.error("Failed to perform transcode", e);
        }
    }

    public static void transcode(Path source, Path target, String transferSyntax) throws IOException {
        Files.createDirectories(target.getParent());
        Files.deleteIfExists(target);

        try (Transcoder transcoder = new Transcoder(source.toFile());
             FileOutputStream outputStream = new FileOutputStream(target.toFile())) {
            transcoder.setIncludeFileMetaInformation(true);
            transcoder.setRetainFileMetaInformation(false);
            transcoder.setEncodingOptions(DicomEncodingOptions.DEFAULT);
            transcoder.setDestinationTransferSyntax(transferSyntax);

            transcoder.transcode((transcoderRef, dataset) -> outputStream);

            log.info("Transcode performed successfully from source: {}", source);
        } catch (Exception e) {
            log.error("Failed to transcode file: {}", source, e);
        }
    }

}
