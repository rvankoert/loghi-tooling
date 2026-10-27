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
     * Size of the image used for the encode/decode capability probe. Must be at least
     * 32x32: the OpenJPEG backend of OpenCV 4.14.0 combined with OpenJPEG 2.4.0 rejects
     * encoding of images whose smallest side is below 32 px ("Number of resolutions is
     * too high in comparison to the size of tiles", because OpenJPEG keeps its default
     * of 6 resolution levels, which requires tiles of at least 2^5 px in each direction,
     * and the encoder uses a single tile covering the whole image). 32x32 is the minimum
     * that passes; 64x64 leaves headroom. OpenCV fixed this upstream in 4.x master
     * (2026-09-23, after the 4.14.0 release) by lowering the number of resolutions for
     * small images.
     */
    private static final int PROBE_IMAGE_SIZE = 64;

    /**
     * Minimal valid JPEG 2000 Part 2 (J2K) codestream: a 32x32 single-channel, 8-bit,
     * constant-gray (128) image, generated with OpenJPEG 2.4.0
     * ({@code opj_compress -i 32x32.pgm -o sample.j2k -numresolution 2}). Used to probe
     * .j2k decoding capability, see {@link #probeSupportedExtensions()}.
     *
     * <p>Package-private so that {@code ImageFormatsTest} can verify the constant itself
     * still decodes to the expected image.
     */
    static final byte[] MINIMAL_J2K_SAMPLE = {
        (byte) 0xff, (byte) 0x4f, (byte) 0xff, (byte) 0x51, (byte) 0x00, (byte) 0x29, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x07, (byte) 0x01, (byte) 0x01, (byte) 0xff, (byte) 0x52, (byte) 0x00,
        (byte) 0x0c, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x05, (byte) 0x04, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0x5c, (byte) 0x00, (byte) 0x13, (byte) 0x40,
        (byte) 0x40, (byte) 0x48, (byte) 0x48, (byte) 0x50, (byte) 0x48, (byte) 0x48, (byte) 0x50, (byte) 0x48, (byte) 0x48, (byte) 0x50, (byte) 0x48, (byte) 0x48, (byte) 0x50, (byte) 0x48, (byte) 0x48, (byte) 0x50,
        (byte) 0xff, (byte) 0x64, (byte) 0x00, (byte) 0x25, (byte) 0x00, (byte) 0x01, (byte) 0x43, (byte) 0x72, (byte) 0x65, (byte) 0x61, (byte) 0x74, (byte) 0x65, (byte) 0x64, (byte) 0x20, (byte) 0x62, (byte) 0x79,
        (byte) 0x20, (byte) 0x4f, (byte) 0x70, (byte) 0x65, (byte) 0x6e, (byte) 0x4a, (byte) 0x50, (byte) 0x45, (byte) 0x47, (byte) 0x20, (byte) 0x76, (byte) 0x65, (byte) 0x72, (byte) 0x73, (byte) 0x69, (byte) 0x6f,
        (byte) 0x6e, (byte) 0x20, (byte) 0x32, (byte) 0x2e, (byte) 0x34, (byte) 0x2e, (byte) 0x30, (byte) 0xff, (byte) 0x90, (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x14, (byte) 0x00, (byte) 0x01, (byte) 0xff, (byte) 0x93, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0xff, (byte) 0xd9
    };

    /**
     * Probes the installed OpenCV build exactly once. For every candidate extension an
     * image is encoded with imencode and decoded again with imdecode; only extensions
     * for which the roundtrip succeeds are considered supported. "j2k" is the exception:
     * it is probed decode-only with {@link #MINIMAL_J2K_SAMPLE}, because OpenCV 4.14.0
     * registers its OpenJPEG encoder only for the ".jp2" extension, so imencode(".j2k")
     * can never succeed there, while .j2k decoding works.
     *
     * <p>The roundtrip uses a {@value #PROBE_IMAGE_SIZE}x{@value #PROBE_IMAGE_SIZE}
     * image instead of 1x1, see {@link #PROBE_IMAGE_SIZE} for the reason. Using the
     * roundtrip (instead of hand-crafted minimal file constants) avoids embedding byte
     * arrays whose validity would be easy to get subtly wrong; the single J2K sample
     * above is guarded by ImageFormatsTest#minimalJ2KSampleDecodesToExpectedImage.
     */
    private static Set<String> probeSupportedExtensions() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Set<String> supported = new HashSet<String>();
        for (String ext : CANDIDATE_EXTENSIONS) {
            boolean probeResult;
            if ("j2k".equals(ext)) {
                probeResult = canDecodeJ2K();
            } else {
                probeResult = canEncodeAndDecodeExtension(ext);
            }
            if (probeResult) {
                supported.add(ext);
            }
        }
        LOG.info("ImageFormats: effective supported image extensions for this OpenCV build: {}",
                describeExtensions(supported));
        return Collections.unmodifiableSet(supported);
    }

    private static boolean canEncodeAndDecodeExtension(String ext) {
        Mat source = new Mat(PROBE_IMAGE_SIZE, PROBE_IMAGE_SIZE, CvType.CV_8UC1);
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

    /**
     * @return true when the installed OpenCV build can decode .j2k files. OpenCV 4.14.0
     *         has no .j2k encoder (the OpenJPEG encoder is registered for ".jp2" only),
     *         so .j2k support can only be verified by decoding the known-valid
     *         {@link #MINIMAL_J2K_SAMPLE} and checking the decoded dimensions.
     */
    private static boolean canDecodeJ2K() {
        Mat decoded = null;
        MatOfByte sample = new MatOfByte(MINIMAL_J2K_SAMPLE);
        try {
            decoded = Imgcodecs.imdecode(sample, Imgcodecs.IMREAD_UNCHANGED);
            return decoded != null
                    && !decoded.empty()
                    && decoded.cols() == 32
                    && decoded.rows() == 32;
        } catch (Throwable t) {
            LOG.warn("ImageFormats: probe for extension 'j2k' failed: {}", t.toString());
            return false;
        } finally {
            sample.release();
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
