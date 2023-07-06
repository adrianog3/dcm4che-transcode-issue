package org.example.opencv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class Unzip {

    private static final Logger log = LoggerFactory.getLogger(Unzip.class);

    private final InputStream sourceAsStream;

    private Path target;
    private boolean replaceIfExists;
    private Filter filter;

    private Unzip(InputStream sourceAsStream) {
        this.sourceAsStream = sourceAsStream;
    }

    /**
     * Create a new instance of {@link Unzip}
     *
     * @param source the path of external zip file
     * @return an instance of {@link Unzip}
     */
    public static Unzip from(Path source) throws FileNotFoundException {
        final InputStream inputStream = new FileInputStream(source.toFile());
        return new Unzip(inputStream);
    }

    /**
     * Create a new instance of {@link Unzip}
     *
     * @param resource the path of resources folder inside jar
     * @return an instance of {@link Unzip}
     */
    public static Unzip from(String resource) {
        final String resourcePath = resource.startsWith("/") ? resource : "/".concat(resource);
        final InputStream resourceStream = Unzip.class.getResourceAsStream(resourcePath);

        if (resourceStream == null) {
            throw new IllegalArgumentException("Resource not found: " + resource);
        }

        return new Unzip(resourceStream);
    }

    /**
     * Create a new instance of {@link Unzip}
     *
     * @param inputStream the zip file stream
     * @return an instance of {@link Unzip}
     */
    public static Unzip from(InputStream inputStream) {
        return new Unzip(inputStream);
    }

    /**
     * Filter files before extraction
     *
     * @param filter implementation
     * @return an instance of {@link Unzip}
     */
    public Unzip withFilter(Filter filter) {
        this.filter = filter;
        return this;
    }

    /**
     * Create a new instance of {@link Unzip}
     *
     * @param replaceIfExists should replace file if exists
     * @return an instance of {@link Unzip}
     */
    public Unzip replaceIfExists(boolean replaceIfExists) {
        this.replaceIfExists = replaceIfExists;
        return this;
    }

    /**
     * Extract files from zip
     *
     * @param target the directory that the files will be extracted
     * @return a list of filtered files
     * @throws IOException              if any I/O operations failed or interrupted
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public List<File> extractTo(String target) throws IOException {
        this.target = Paths.get(target);
        return extract();
    }

    private List<File> extract() throws IOException {
        final List<File> files = new ArrayList<>();

        try (ZipInputStream zipInputStream = new ZipInputStream(sourceAsStream)) {
            ZipEntry zipEntry;

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {
                    Path newPath = resolvePath(zipEntry.getName());
                    Files.createDirectories(newPath);
                    continue;
                }

                extractFile(zipInputStream, zipEntry, files);
            }
        }

        sourceAsStream.close();

        return files;
    }

    private void extractFile(ZipInputStream zipInputStream, ZipEntry zipEntry, List<File> files) throws IOException {
        final String fileName = filter != null
            ? filter.doFilter(zipEntry.getName())
            : zipEntry.getName();

        if (fileName == null) {
            log.debug("The following entry will not be extracted \"{}\"", zipEntry.getName());
            return;
        }

        Path newPath = resolvePath(fileName);

        if (newPath.getParent() != null && Files.notExists(newPath.getParent())) {
            Files.createDirectories(newPath.getParent());
        }

        if (!Files.exists(newPath) || replaceIfExists) {
            Files.copy(zipInputStream, newPath, StandardCopyOption.REPLACE_EXISTING);
        }

        files.add(newPath.toFile());
    }

    private Path resolvePath(String entryName) {
        return target.resolve(entryName).normalize();
    }

    public interface Filter {

        /**
         * Handle the file before extracting
         *
         * @param fileName the name of file from zip
         * @return the new name of the file. If null, the file will not be extracted
         */
        String doFilter(String fileName);

    }

}
