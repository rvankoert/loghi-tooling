package nl.knaw.huc.di.images.layoutds.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class KrakenModelData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private byte[] modelData;
    private String originalFileName;
    @ManyToOne(targetEntity = KrakenModel.class)
    private KrakenModel model;

    public void setModelData(byte[] modelData) {
        this.modelData = modelData;
    }

    public byte[] getModelData() {
        return modelData;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setModel(KrakenModel model) {
        this.model = model;
    }

    public KrakenModel getModel() {
        return model;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
