package nl.knaw.huc.di.images.layoutds.models.pim;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Blob;
import java.util.UUID;

@Entity
public class SiameseNetworkModel implements IPimObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private UUID uuid;
    private String label;

    @JsonIgnore
    @Lob
    private Blob data;
    private String originalFileName;


    public SiameseNetworkModel() {
        this.uuid = UUID.randomUUID();
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Blob getData() {
        return data;
    }

    public void setData(Blob data) {
        this.data = data;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }
}
