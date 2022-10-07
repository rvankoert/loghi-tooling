package nl.knaw.huc.di.images.layoutds.models.pim;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.knaw.huc.di.images.layoutds.models.Language;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;


@Entity
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"uri", "languageid"})
)
public class Tesseract4Model implements IPimObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date created;
    private Date updated;
    private Date deleted;

    @Column(nullable = false)
    private String uri;

    @Column(nullable = false, unique = true)
    private UUID uuid;
    private String title;

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


    @ManyToOne(targetEntity = Language.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "languageId")
    private Language language;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tesseract4ModelData> models = new ArrayList<>();

    public Tesseract4Model() {
        this.uuid = UUID.randomUUID();
        this.created = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    @JsonIgnore
    public Map<String, byte[]> getModelData() {
        return models.stream()
                .collect(Collectors.toMap(Tesseract4ModelData::getOriginalFileName, Tesseract4ModelData::getModelData));
    }

    public void addModelData(byte[] modelData, String originalFileName) {
        Tesseract4ModelData tesseract4ModelData;
        final Optional<Tesseract4ModelData> modelOpt = models.stream()
                .filter(model -> model.getOriginalFileName()
                        .equals(originalFileName)).findAny();
        if (modelOpt.isPresent()) {
            tesseract4ModelData = modelOpt.get();
        } else {
            tesseract4ModelData = new Tesseract4ModelData();
            tesseract4ModelData.setModel(this);
            models.add(tesseract4ModelData);
        }
        tesseract4ModelData.setModelData(modelData);
        tesseract4ModelData.setOriginalFileName(originalFileName);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}