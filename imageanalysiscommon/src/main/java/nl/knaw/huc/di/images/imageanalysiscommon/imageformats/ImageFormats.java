package nl.knaw.huc.di.images.imageanalysiscommon.imageformats;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Single source of truth for the image formats accepted by every Loghi minion that takes
 * images as input.
 *
 * <p>The effective supported set is the intersection of {@link #CANDIDATE_EXTENSIONS}
 * (the formats the project wants to accept) with what the installed OpenCV build can
 * actually decode. That capability is probed exactly once (imencode-then-imdecode
 * roundtrip per extension) on first use and logged. Every minion MUST use
 * {@link #isSupportedImage(Path)} (or {@link #isSupportedImageBytes(byte[])} for
 * in-memory data) instead of its own extension check, so that all minions accept
 * exactly the same formats.
 */
public final class ImageFormats {

    private static final Logger LOG = LoggerFactory.getLogger(ImageFormats.class);

    /**
     * Formats the project wants to accept, in the priority order used by
     * {@link #resolvePairedImage(Path, String)}. "jp2"/"j2k" are only accepted when the
     * runtime probe proves the installed OpenCV build has JPEG2000 support.
     */
    public static final List<String> CANDIDATE_EXTENSIONS =
            Collections.unmodifiableList(Arrays.asList(
                    "jpg", "jpeg", "png", "tif", "tiff", "jp2", "j2k"));

    private static final byte[] PNG_MAGIC = {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] JPEG_MAGIC = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] TIFF_LE_MAGIC = {0x49, 0x49, 0x2A, 0x00};
    private static final byte[] TIFF_BE_MAGIC = {0x4D, 0x4D, 0x00, 0x2A};
    private static final byte[] JP2_MAGIC = {
            0x00, 0x00, 0x00, 0x0C, 0x6A, 0x50, 0x20, 0x20, 0x0D, 0x0A, (byte) 0x87, 0x0A};

    private static volatile Set<String> effectiveSupportedExtensions;

    private ImageFormats() {
    }

    /**
     * @return the effective supported extensions (lowercase, without leading dot).
     *         Computed once, on first call, by probing the installed OpenCV build.
     */
    public static Set<String> getSupportedExtensions() {
        Set<String> local = effectiveSupportedExtensions;
        if (local == null) {
            synchronized (ImageFormats.class) {
                if (effectiveSupportedExtensions == null) {
                    effectiveSupportedExtensions = probeSupportedExtensions();
                }
                local = effectiveSupportedExtensions;
            }
        }
        return local;
    }

    /**
     * @return true when the file's extension (case-insensitive, without dot) is in the
     *         effective supported set.
     */
    public static boolean isSupportedImage(Path file) {
        String extension = extensionOf(file);
        return extension != null && getSupportedExtensions().contains(extension);
    }

    /**
     * @return the lowercase extension of the file (without dot), or null when the file
     *         name has no (usable) extension.
     */
    public static String extensionOf(Path file) {
        if (file == null || file.getFileName() == null) {
            return null;
        }
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) {
            return null;
        }
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    /**
     * Content-based check (magic bytes). Use for in-memory image data (e.g. web service
     * uploads) where no file extension is available.
     */
    public static boolean isSupportedImageBytes(byte[] data) {
        if (data == null) {
            return false;
        }
        Set<String> supported = getSupportedExtensions();
        if (startsWith(data, PNG_MAGIC) && supported.contains("png")) {
            return true;
        }
        if (startsWith(data, JPEG_MAGIC)
                && (supported.contains("jpg") || supported.contains("jpeg"))) {
            return true;
        }
        if ((startsWith(data, TIFF_LE_MAGIC) || startsWith(data, TIFF_BE_MAGIC))
                && (supported.contains("tif") || supported.contains("tiff"))) {
            return true;
        }
        if (startsWith(data, JP2_MAGIC)
                && (supported.contains("jp2") || supported.contains("j2k"))) {
            return true;
        }
        return false;
    }

    /**
     * Finds the image belonging to a base name (the name a PAGE XML file has without
     * ".xml") inside a directory, using the candidate priority order (jpg, jpeg, png,
     * tif, tiff, jp2, j2k — restricted to the effective supported set).
     *
     * @return the first regular file named {@code baseFilename + "." + ext} for a
     *         supported extension, or {@link Optional#empty()} when none exists.
     */
    public static Optional<Path> resolvePairedImage(Path dir, String baseFilename) {
        if (dir == null || baseFilename == null || !Files.isDirectory(dir)) {
            return Optional.empty();
        }
        Set<String> supported = getSupportedExtensions();
        for (String ext : CANDIDATE_EXTENSIONS) {
            if (!supported.contains(ext)) {
                continue;
            }
            Path candidate = dir.resolve(baseFilename + "." + ext);
            if (Files.isRegularFile(candidate)) {
                return Optional.of(candidate);
            }
        }
        return Optional.empty();
    }

    /**
     * @return a human readable list such as {@code ".jpg, .jpeg, .png, .tif, .tiff"},
     *         suitable for CLI help text and error messages.
     */
    public static String describeSupportedFormats() {
        return describeExtensions(getSupportedExtensions());
    }

    static String describeExtensions(Set<String> extensions) {
        List<String> ordered = new ArrayList<String>();
        for (String ext : CANDIDATE_EXTENSIONS) {
            if (extensions.contains(ext)) {
                ordered.add("." + ext);
            }
        }
        return String.join(", ", ordered);
    }

    /**
     * Probes the installed OpenCV build exactly once. For every candidate extension a
     * 1x1 image is encoded with imencode and decoded again with imdecode; only
     * extensions for which the roundtrip succeeds are considered supported. Using the
     * roundtrip (instead of hand-crafted minimal file constants) avoids embedding byte
     * arrays whose validity would be easy to get subtly wrong.
     */
    private static Set<String> probeSupportedExtensions() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Set<String> supported = new HashSet<String>();
        for (String ext : CANDIDATE_EXTENSIONS) {
            if (canDecodeExtension(ext)) {
                supported.add(ext);
            }
        }
        LOG.info("ImageFormats: effective supported image extensions for this OpenCV build: {}",
                describeExtensions(supported));
        return Collections.unmodifiableSet(supported);
    }

    private static boolean canDecodeExtension(String ext) {
        Mat source = new Mat(1, 1, CvType.CV_8UC1);
        MatOfByte encoded = new MatOfByte();
        Mat decoded = null;
        try {
            source.put(0, 0, (byte) 128);
            Imgcodecs.imencode("." + ext, source, encoded);
            if (encoded.empty()) {
                return false; // codec not present in this OpenCV build
            }
            decoded = Imgcodecs.imdecode(encoded, Imgcodecs.IMREAD_UNCHANGED);
            return decoded != null && !decoded.empty();
        } catch (Throwable t) {
            LOG.warn("ImageFormats: probe for extension '{}' failed: {}", ext, t.toString());
            return false;
        } finally {
            source.release();
            encoded.release();
            if (decoded != null) {
                decoded.release();
            }
        }
    }

    private static boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }
}
