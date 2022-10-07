package nl.knaw.huc.di.images.layoutds.models.pim;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class LoghiLineDetectionModel implements IPimObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private UUID uuid;
    private String label;

    @JsonIgnore
    @Lob
    private byte[] data;
    private String originalFileName;

    public LoghiLineDetectionModel() {
        uuid = UUID.randomUUID();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
}
