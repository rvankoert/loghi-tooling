package nl.knaw.huc.di.images.imageanalysiscommon.network;

public class ImageFile {
    private byte[] bytes;
    private String fileName;
    private String extension;

    public ImageFile(byte[] bytes, String fileName, String extension) {
        this.bytes = bytes;
        this.fileName = fileName;
        this.extension = extension;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getFileName() {
        return fileName;
    }

    public String getExtension() {
        return extension;
    }

    public void release() {
        this.bytes = null;
        this.fileName = null;
        this.extension = null;
    }
}
