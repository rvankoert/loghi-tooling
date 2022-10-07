package nl.knaw.huc.di.images.layoutds.models;

public enum TranscriptionFormat {
    NONE(0),
    Alto(1),
    Page(2),
    Hocr(3),
    AbbyyXml(4),
    PlainText(5);


    private final int value;

    TranscriptionFormat(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public int getTranscriberValue() {
        TranscriptionFormat transcriber = TranscriptionFormat.NONE; // Or whatever
        return transcriber.getValue();
    }

}
