package nl.knaw.huc.di.images.layoutds.models;

public class HTRConfig {
    private String model;
    private String batchSize;

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setBatchSize(String batchSize) {
        this.batchSize = batchSize;
    }

    public String getBatchSize() {
        return batchSize;
    }

    public String toString() {
        String result = "";
        result += "model=" + model + "\n";
        result += "batch_size=" + batchSize + "\n";
        return result;
    }
}
