package nl.knaw.huc.di.images.layoutds.models;

import java.util.Map;

public class LaypaConfig {
    private String model;
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
}
