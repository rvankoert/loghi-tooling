package nl.knaw.huc.di.images.imageanalysiscommon.imageformats;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * The single image decoder used by all Loghi minions.
 *
 * <p>Every read first passes the {@link ImageFormats} extension gate and then decodes
 * with OpenCV. If decoding yields an empty Mat (corrupt file, codec missing from this
 * OpenCV build, ...) an {@link UnsupportedImageException} with an actionable message is
 * thrown instead of returning an empty Mat.
 *
 * <p>Uses {@code Imgcodecs.imread} directly (not {@code OpenCVWrapper}, which lives in
 * the higher-level {@code layoutanalyzer} module — see plan section 0.5) but mirrors
 * its contract: the destination Mat allocation is synchronized so concurrent minion
 * worker threads cannot race on the OpenCV JNI allocator, and ownership of the returned
 * Mat is transferred to the caller, who must release it (e.g. {@code mat.release()} in
 * a finally block).
 */
public final class ImageReader {

    private static final Logger LOG = LoggerFactory.getLogger(ImageReader.class);

    private static final Object ALLOCATION_LOCK = new Object();

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private ImageReader() {
    }

    /** Decodes the file as single-channel grayscale (replaces IMREAD_GRAYSCALE reads). */
    public static Mat readGray(Path file) throws UnsupportedImageException {
        return read(file, Imgcodecs.IMREAD_GRAYSCALE);
    }

    /** Decodes the file as 3-channel BGR (replaces default / IMREAD_COLOR reads). */
    public static Mat readColor(Path file) throws UnsupportedImageException {
        return read(file, Imgcodecs.IMREAD_COLOR);
    }

    /** Decodes the file preserving channels/depth (replaces IMREAD_UNCHANGED reads). */
    public static Mat readUnchanged(Path file) throws UnsupportedImageException {
        return read(file, Imgcodecs.IMREAD_UNCHANGED);
    }

    private static Mat read(Path file, int flags) throws UnsupportedImageException {
        if (file == null) {
            throw new UnsupportedImageException("Image path is null");
        }
        if (!ImageFormats.isSupportedImage(file)) {
            throw new UnsupportedImageException(
                    "Unsupported image type '" + ImageFormats.extensionOf(file)
                            + "' for file '" + file + "'. Supported types: "
                            + ImageFormats.describeSupportedFormats() + ".");
        }
        Mat destination;
        synchronized (ALLOCATION_LOCK) {
            destination = new Mat();
            Imgcodecs.imread(file.toAbsolutePath().toString(), destination, flags);
        }
        if (destination.empty()) {
            destination.release();
            LOG.error("Could not decode image file '{}'", file.toAbsolutePath());
            throw new UnsupportedImageException(
                    "Could not decode image file '" + file + "' (corrupt file or codec not "
                            + "available in the installed OpenCV build). Supported types: "
                            + ImageFormats.describeSupportedFormats() + ".");
        }
        return destination;
    }
}
