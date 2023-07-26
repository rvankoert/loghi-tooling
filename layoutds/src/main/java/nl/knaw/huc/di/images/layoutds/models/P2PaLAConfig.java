package nl.knaw.huc.di.images.layoutds.models;

import java.util.Map;
import java.util.UUID;

public class P2PaLAConfig {
    private String gitHash;
    private String model;
    private UUID uuid;
    private Map<String, Object> values;
    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
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
        return result;
    }

    public void setGitHash(String gitHash) {
        this.gitHash = gitHash;
    }

    public String getGitHash() {
        return gitHash;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
