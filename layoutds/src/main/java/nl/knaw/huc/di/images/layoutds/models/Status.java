package nl.knaw.huc.di.images.layoutds.models;

public enum Status {
    NONE(0),
    New(1),
    InProgress(2),
    LayoutAnalyzed(3),
    LayoutCorrected(4),
    GroundTruth(5),
    BaselinesDetected(6),
    BaselinesCorrected(7),
    Final(8),
    Done(9);


    private final int value;

    Status(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public int getStatusValue() {
        Status status = Status.NONE; // Or whatever
        return status.getValue();
    }

}
