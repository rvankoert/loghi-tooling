package nl.knaw.huc.di.images.layoutds.models;

import java.util.Map;
import java.util.UUID;

public class HTRConfig {
    private String model;
    private String batchSize;

    private Map<String, Object> values;
    private String githash;
    private UUID uuid;
    private String modelName;
    private String urlCode;

    public HTRConfig() {

    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(String batchSize) {
        this.batchSize = batchSize;
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

    public String getGithash() {
        return githash;
    }

    public void setGithash(String githash) {
        this.githash = githash;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }

    public String getUrlCode() {
        return urlCode;
    }

    public void setUrlCode(String urlCode) {
        this.urlCode = urlCode;
    }
}
