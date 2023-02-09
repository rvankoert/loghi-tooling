package nl.knaw.huc.di.images.layoutds.models;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Map;

public class HTRConfig {
    private String model;
    private String batchSize;

    private Map<String, Object> values;
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

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    public String toString() {
        String result = "";
        result += "model=" + model + "\n";
        result += "batch_size=" + batchSize + "\n";
        return result;
    }
}
