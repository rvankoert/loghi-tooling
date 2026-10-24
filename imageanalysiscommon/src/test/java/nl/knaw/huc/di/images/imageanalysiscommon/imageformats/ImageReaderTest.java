package nl.knaw.huc.di.images.imageanalysiscommon.imageformats;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ImageReaderTest {

    @BeforeClass
    public static void loadOpenCv() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    /** Encodes a small 3x3 grayscale image with the given OpenCV extension and writes it to disk. */
    private static Path writeImage(Path dir, String name, String ext) throws IOException {
        Mat src = new Mat(3, 3, CvType.CV_8UC1);
        MatOfByte buf = new MatOfByte();
        try {
            src.put(0, 0, (byte) 10);
            src.put(1, 1, (byte) 200);
            Assert.assertTrue("imencode " + ext + " should succeed",
                    Imgcodecs.imencode(ext, src, buf));
            Path out = dir.resolve(name + ext);
            Files.write(out, buf.toArray());
            return out;
        } finally {
            src.release();
            buf.release();
        }
    }

    private static void deleteRecursively(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted((a, b) -> b.getNameCount() - a.getNameCount()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException ignored) {
                    // best effort cleanup in tests
                }
            });
        }
    }

    @Test
    public void readGrayDecodesPng() throws Exception {
        Path dir = Files.createTempDirectory("imagereader-");
        try {
            Path png = writeImage(dir, "page", ".png");
            Mat gray = ImageReader.readGray(png);
            try {
                Assert.assertFalse(gray.empty());
                Assert.assertEquals(1, gray.channels());
                Assert.assertEquals(3, gray.rows());
                Assert.assertEquals(3, gray.cols());
            } finally {
                gray.release();
            }
        } finally {
            deleteRecursively(dir);
        }
    }

    @Test
    public void readColorDecodesJpeg() throws Exception {
        Path dir = Files.createTempDirectory("imagereader-");
        try {
            Path jpg = writeImage(dir, "page", ".jpg");
            Mat color = ImageReader.readColor(jpg);
            try {
                Assert.assertFalse(color.empty());
                Assert.assertEquals(3, color.channels());
                Assert.assertEquals(3, color.rows());
                Assert.assertEquals(3, color.cols());
            } finally {
                color.release();
            }
        } finally {
            deleteRecursively(dir);
        }
    }

    @Test
    public void readColorDecodesTiffWhenBuildSupportsIt() throws Exception {
        // Gated on the runtime probe: a build without the TIFF codec simply skips this test.
        Assume.assumeTrue("OpenCV build has no TIFF support",
                ImageFormats.getSupportedExtensions().contains("tif"));
        Path dir = Files.createTempDirectory("imagereader-");
        try {
            Path tif = writeImage(dir, "page", ".tif");
            Mat color = ImageReader.readColor(tif);
            try {
                Assert.assertFalse(color.empty());
                Assert.assertEquals(3, color.channels());
                Assert.assertEquals(3, color.rows());
            } finally {
                color.release();
            }
        } finally {
            deleteRecursively(dir);
        }
    }

    @Test
    public void unsupportedExtensionThrows() throws Exception {
        Path dir = Files.createTempDirectory("imagereader-");
        try {
            Path xml = dir.resolve("page.xml");
            Files.write(xml, "<xml/>".getBytes(StandardCharsets.UTF_8));
            try {
                ImageReader.readColor(xml);
                Assert.fail("expected UnsupportedImageException");
            } catch (UnsupportedImageException e) {
                Assert.assertTrue("message should list supported types: " + e.getMessage(),
                        e.getMessage().contains("Supported types"));
            }
        } finally {
            deleteRecursively(dir);
        }
    }

    @Test
    public void corruptFileWithImageExtensionThrows() throws Exception {
        Path dir = Files.createTempDirectory("imagereader-");
        try {
            Path fake = dir.resolve("fake.png");
            Files.write(fake, "definitely not a png".getBytes(StandardCharsets.UTF_8));
            try {
                ImageReader.readGray(fake);
                Assert.fail("expected UnsupportedImageException");
            } catch (UnsupportedImageException e) {
                Assert.assertTrue("message should mention decoding: " + e.getMessage(),
                        e.getMessage().contains("Could not decode"));
            }
        } finally {
            deleteRecursively(dir);
        }
    }

    @Test
    public void nullPathThrows() {
        try {
            ImageReader.readGray(null);
            Assert.fail("expected UnsupportedImageException");
        } catch (UnsupportedImageException expected) {
            // expected
        }
    }
}
