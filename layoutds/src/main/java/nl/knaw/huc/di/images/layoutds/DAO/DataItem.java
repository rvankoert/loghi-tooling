package nl.knaw.huc.di.images.layoutds.DAO;

/**
 * Created by rutger on 25-4-17.
 */
public class DataItem {
    String key;
    Double value;

    DataItem(String key, Double value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
