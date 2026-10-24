package nl.knaw.huc.di.images.imageanalysiscommon.imageformats;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Stream;

public class ImageFormatsTest {

    @BeforeClass
    public static void loadOpenCv() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void supportedSetIsSubsetOfCandidates() {
        Set<String> supported = ImageFormats.getSupportedExtensions();
        for (String ext : supported) {
            Assert.assertTrue("unexpected extension in supported set: " + ext,
                    ImageFormats.CANDIDATE_EXTENSIONS.contains(ext));
        }
    }

    @Test
    public void pngAndJpgAreSupportedInAnySaneBuild() {
        Set<String> supported = ImageFormats.getSupportedExtensions();
        Assert.assertTrue(supported.contains("png"));
        Assert.assertTrue(supported.contains("jpg"));
        Assert.assertTrue(supported.contains("jpeg"));
    }

    @Test
    public void isSupportedImageIsCaseInsensitive() {
        Assert.assertTrue(ImageFormats.isSupportedImage(Paths.get("/tmp/page.PNG")));
        Assert.assertTrue(ImageFormats.isSupportedImage(Paths.get("foo.JPG")));
        Assert.assertTrue(ImageFormats.isSupportedImage(Paths.get("bar.TIFF")));
    }

    @Test
    public void isSupportedImageRejectsNonImageExtensions() {
        Assert.assertFalse(ImageFormats.isSupportedImage(Paths.get("page.xml")));
        Assert.assertFalse(ImageFormats.isSupportedImage(Paths.get("notes.txt")));
        Assert.assertFalse(ImageFormats.isSupportedImage(Paths.get("noextension")));
        Assert.assertFalse(ImageFormats.isSupportedImage(Paths.get("trailing.dot.")));
        // bmp is not part of the project's format policy, even if OpenCV could decode it
        Assert.assertFalse(ImageFormats.isSupportedImage(Paths.get("archive.bmp")));
    }

    @Test
    public void extensionOfHandlesEdgeCases() {
        Assert.assertEquals("png", ImageFormats.extensionOf(Paths.get("a/b/c.png")));
        Assert.assertEquals("png", ImageFormats.extensionOf(Paths.get("c.PNG")));
        Assert.assertNull(ImageFormats.extensionOf(Paths.get("noextension")));
        Assert.assertNull(ImageFormats.extensionOf(Paths.get("trailing.dot.")));
        Assert.assertNull(ImageFormats.extensionOf(null));
    }

    @Test
    public void magicSniffDetectsEncodedBytes() {
        Mat tiny = new Mat(1, 1, CvType.CV_8UC1);
        MatOfByte pngBuf = new MatOfByte();
        MatOfByte jpgBuf = new MatOfByte();
        try {
            tiny.put(0, 0, (byte) 128);
            Assert.assertTrue(Imgcodecs.imencode(".png", tiny, pngBuf));
            Assert.assertTrue(Imgcodecs.imencode(".jpg", tiny, jpgBuf));
            Assert.assertTrue(ImageFormats.isSupportedImageBytes(pngBuf.toArray()));
            Assert.assertTrue(ImageFormats.isSupportedImageBytes(jpgBuf.toArray()));
            byte[] notAnImage = "this is not an image".getBytes(StandardCharsets.UTF_8);
            Assert.assertFalse(ImageFormats.isSupportedImageBytes(notAnImage));
            Assert.assertFalse(ImageFormats.isSupportedImageBytes(new byte[2]));
            Assert.assertFalse(ImageFormats.isSupportedImageBytes(null));
        } finally {
            pngBuf.release();
            jpgBuf.release();
            tiny.release();
        }
    }

    @Test
    public void resolvePairedImageFindsFirstSupportedMatch() throws IOException {
        Path dir = Files.createTempDirectory("imageformats-");
        try {
            Files.write(dir.resolve("stem.png"), new byte[] {1});
            Assert.assertEquals(dir.resolve("stem.png").toString(),
                    ImageFormats.resolvePairedImage(dir, "stem").orElse(null).toString());
            Assert.assertFalse(ImageFormats.resolvePairedImage(dir, "missing").isPresent());
            if (ImageFormats.getSupportedExtensions().contains("tif")) {
                Files.write(dir.resolve("stem2.tif"), new byte[] {1});
                Assert.assertEquals(dir.resolve("stem2.tif").toString(),
                        ImageFormats.resolvePairedImage(dir, "stem2").orElse(null).toString());
            }
        } finally {
            deleteRecursively(dir);
        }
    }

    @Test
    public void describeSupportedFormatsMentionsPng() {
        Assert.assertTrue(ImageFormats.describeSupportedFormats(),
                ImageFormats.describeSupportedFormats().contains(".png"));
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
}
