package nl.knaw.huc.di.images.layoutds.models.pim;

import nl.knaw.huc.di.images.layoutds.models.DocumentImage;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class SiameseImageModelJob implements IPimObject{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private DocumentImage documentImage;
    private String modelName;
    private UUID uuid;

    public SiameseImageModelJob() {
        this.uuid = UUID.randomUUID();
    }

    public DocumentImage getDocumentImage() {
        return documentImage;
    }

    public void setDocumentImage(DocumentImage documentImage) {
        this.documentImage = documentImage;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
