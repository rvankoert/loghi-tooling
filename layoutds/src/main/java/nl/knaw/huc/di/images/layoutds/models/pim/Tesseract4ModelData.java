package nl.knaw.huc.di.images.layoutds.models.pim;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
public class Tesseract4ModelData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Tesseract4Model model;

    @Lob
    private byte[] modelData;

    private UUID uuid;
    private String originalFileName;

    public Tesseract4ModelData() {
        this.uuid = UUID.randomUUID();
    }

    public Tesseract4Model getModel() {
        return model;
    }

    public void setModel(Tesseract4Model model) {
        this.model = model;
    }

    public byte[] getModelData() {
        return modelData;
    }

    public void setModelData(byte[] modelData) {
        this.modelData = modelData;
    }

    private Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }
}
