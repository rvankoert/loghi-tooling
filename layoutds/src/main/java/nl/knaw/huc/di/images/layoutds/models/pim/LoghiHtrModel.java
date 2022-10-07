package nl.knaw.huc.di.images.layoutds.models.pim;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.knaw.huc.di.images.layoutds.jpahelpers.HashMapConverter;

import javax.persistence.*;
import java.util.Map;
import java.util.UUID;

@Entity
public class LoghiHtrModel implements IPimObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private UUID uuid;
    private String label;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    private LoghiHtrModelData data;

    @Lob
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> config;

    @Column(columnDefinition = "boolean default false")
    private boolean publicModel;

    @ManyToOne(targetEntity = PimUser.class, optional = true)
    @JoinColumn(name = "owner_id")
    private PimUser owner;

    public LoghiHtrModel() {
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

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public boolean isPublicModel() {
        return publicModel;
    }

    public void setPublicModel(boolean publicModel) {
        this.publicModel = publicModel;
    }

    public PimUser getOwner() {
        return owner;
    }

    public void setOwner(PimUser owner) {
        this.owner = owner;
    }

    public LoghiHtrModelData getData() {
        return data;
    }

    public void setData(LoghiHtrModelData data) {
        this.data = data;
    }
}
