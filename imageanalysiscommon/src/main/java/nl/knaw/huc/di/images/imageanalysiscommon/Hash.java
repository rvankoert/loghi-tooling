package nl.knaw.huc.di.images.imageanalysiscommon;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    final private static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String getSHA512(String filename) throws IOException {
        return Files.asByteSource(new File(filename)).hash(Hashing.sha512()).toString();
    }

    public static String getSHA512(byte[] input) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.reset();
        digest.update(input);
        String toReturn = String.format("%0128x", new BigInteger(1, digest.digest()));
        return toReturn;
    }

}
