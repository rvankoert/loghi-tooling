package nl.knaw.huc.di.images.layoutds.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
public class KrakenModel implements IPimObject {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uri;
    private UUID uuid;

    @ManyToOne(targetEntity = Language.class)
    private Language language;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private KrakenModelData data;
    private String title;
    private String modelName;

    public KrakenModel() {
        this.uuid = UUID.randomUUID();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public Map<String, byte[]> getModelData() {
        final HashMap<String, byte[]> data = new HashMap<>();
        data.put(this.data.getOriginalFileName(), this.data.getModelData());
        return data;
    }

    public void setModelData(byte[] modelData, String originalFileName) {
        if(data == null) {
            data = new KrakenModelData();
        }

        data.setModel(this);
        data.setModelData(modelData);
        data.setOriginalFileName(originalFileName);
        this.modelName = originalFileName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getModelName() {
        return modelName;
    }

}
