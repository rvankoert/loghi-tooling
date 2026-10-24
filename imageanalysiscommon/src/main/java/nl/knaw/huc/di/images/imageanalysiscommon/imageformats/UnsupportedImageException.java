package nl.knaw.huc.di.images.imageanalysiscommon.imageformats;

/**
 * Thrown when a minion encounters an image file whose format is not in the shared
 * {@link ImageFormats} supported set, or which cannot be decoded by the installed
 * OpenCV build.
 *
 * <p>Extends {@link RuntimeException} (deliberately not IOException) because the
 * exception is raised from {@code Supplier<Mat>} lambdas, whose functional method
 * declares no checked exceptions. Affected minion {@code run()} methods catch it
 * explicitly and log it per-file, mirroring how they already handle IOException.
 */
public class UnsupportedImageException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnsupportedImageException(String message) {
        super(message);
    }

    public UnsupportedImageException(String message, Throwable cause) {
        super(message, cause);
    }
}
