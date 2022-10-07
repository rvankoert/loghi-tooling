package nl.knaw.huc.di.images.layoutds.models;

public enum Transcriber {
    NONE(0),
    Tesseract4(1),
    ABBBYY(2),
    Transkribus(3),
    CustomTesseractPageXML(4),
    Other(5),
    LayoutPageXML(6),
    CCSdocWORKS(7),
    GroundTruth(8),
    RecalculateReadingOrder(9),
    Loghi(10)
    ;


    private final int value;

    Transcriber(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public int getTranscriberValue() {
        Transcriber transcriber = Transcriber.NONE; // Or whatever
        return transcriber.getValue();
    }

}
