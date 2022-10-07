package nl.knaw.huc.di.images.imageanalysiscommon.network;

import com.google.common.base.Strings;
import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class WebTooling {

    public static void readRemoteImageToStream(String remoteUri, OutputStream outputStream) throws IOException {
        if (!remoteUri.endsWith(".jpg") &&
                !remoteUri.endsWith(".jpeg") &&
                !remoteUri.endsWith(".png") &&
                !remoteUri.endsWith(".tif")
        ) {
            remoteUri += "/full/full/0/default.jpg";
            System.out.println("getting remote resource: " + remoteUri);
        }

        URL url = new URL(remoteUri);
        try (InputStream is = url.openStream()) {
            byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
            int n;

            while ((n = is.read(byteChunk)) > 0) {
                outputStream.write(byteChunk, 0, n);
            }
        }
    }

    public static ImageFile readRemoteImageAsStream(String uri) throws Exception {
        URL url = new URL(uri);
        for (int i = 0; i < 3; i++) {
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String fileName = "";
                String disposition = httpConn.getHeaderField("Content-Disposition");
                String extension;
                if (disposition != null) {
// extracts file name from header fieldreadRemoteImageAsStream
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10,
                                disposition.length() - 1);
                    }
                } else {
// extracts file name from URL
                    fileName = uri.substring(uri.lastIndexOf("/") + 1);
                }
                extension = FilenameUtils.getExtension(fileName);
                if (Strings.isNullOrEmpty(extension) && uri.endsWith("/jpg")) {
                    extension = "jpg";
                }

                try (InputStream is = url.openStream()) {
                    byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
                    int n;
                    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

                        while ((n = is.read(byteChunk)) > 0) {
                            byteArrayOutputStream.write(byteChunk, 0, n);
                        }
                        ImageFile imageFile = new ImageFile(byteArrayOutputStream.toByteArray(), fileName, extension);
                        byteArrayOutputStream.close();
                        return imageFile;
                    }
                } catch (IOException e) {
                    System.err.printf("Failed while reading bytes from %s: %s%n", url.toExternalForm(), e.getMessage());
                    e.printStackTrace();
                    // Perform any other exception handling that's appropriate.
                }
            } else {
                System.err.println(uri);
                System.err.println("responsecode not HTTP_OK");
            }
            // Something went wrong: just sleep for i seconds
            Thread.sleep(i * 1000);
        }
        throw new Exception("readRemoteImageAsStream too many errors");

    }

    public static Mat readImage(String uri, UUID uuid, boolean isIIIF) throws Exception {
        System.out.println("readImage: " + uri);
        if (uri.startsWith("http")) {
            return readRemoteImage(uri, uuid, false, isIIIF);
        }
        return Imgcodecs.imread(uri);
    }

    public static Mat readRemoteImage(String uri, UUID uuid, boolean localOnly, boolean isIIIF) throws Exception {
        System.out.println("readRemoteImage: " + uri);
        int errorCount = 0;
        String baseDir = "/scratch/preloaded/";
        String targetFileWithoutExtension = null;
        if (uuid != null) {
            baseDir += uuid.toString().substring(0, 2) + "/";
            if (!Files.exists(Paths.get(baseDir))) {
                new File(baseDir).mkdir();
            }
            baseDir += uuid.toString().substring(2, 4) + "/";
            if (!Files.exists(Paths.get(baseDir))) {
                new File(baseDir).mkdir();
            }

            targetFileWithoutExtension = baseDir + uuid.toString();
            File jpgFile = new File(targetFileWithoutExtension + ".jpg");
            if (jpgFile.exists()) {
                return Imgcodecs.imread(jpgFile.getAbsolutePath());
            }

            File pngFile = new File(targetFileWithoutExtension + ".png");
            if (pngFile.exists()) {
                return Imgcodecs.imread(pngFile.getAbsolutePath());
            }
        }
        if (!localOnly) {
            // guess the iiif uri of the image
            if (uri != null && !uri.endsWith(".jpg") &&
                    !uri.endsWith(".jpeg") &&
                    !uri.endsWith(".png") &&
                    !uri.endsWith(".tif") &&
                    !uri.endsWith("/jpg") &&
                    isIIIF
            ) {
                uri += "/full/full/0/default.jpg";
            }

            try {
                ImageFile imageFile = readRemoteImageAsStream(uri);
                errorCount++;
                MatOfByte matOfByte = new MatOfByte(imageFile.getBytes());
                Mat image = Imgcodecs.imdecode(matOfByte, 1);
                matOfByte.release();
                if (image.width() == 0 || image.height() == 0) {
                    System.err.printf("image %s has zero height and/or width%n", uri);
                    throw new Exception(String.format("image %s has zero height and/or width", uri));
                }
                if (new File(baseDir).exists() && targetFileWithoutExtension != null) {
                    String extension = imageFile.getExtension();
                    if (Strings.isNullOrEmpty(extension)) {
                        extension = "png";
                    }
                    Files.write(Paths.get(targetFileWithoutExtension + "." + extension), imageFile.getBytes());
                }
                return image;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        throw new Exception("Too many errors");
    }

}
