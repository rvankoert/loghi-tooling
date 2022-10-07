package nl.knaw.huc.di.images.layoutds.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.knaw.huc.di.images.layoutds.PublicAnnotation;
import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;


@Entity
@XmlRootElement

public class MetaData implements IPimObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @PublicAnnotation(readOnly = true, uuid = "9efc24f7-594f-4a8a-8b72-50ba1f5b5408", label = "UUID")
    private UUID uuid;

    private String label;
    private String value;

    @ManyToOne(targetEntity = DocumentImage.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parentId")
    @JsonIgnore
    private DocumentImage parent;

    public MetaData() {
        uuid = UUID.randomUUID();
    }

    public MetaData(DocumentImage parent, String label, String value) {
        this();
        this.parent = parent;
        this.label = label;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DocumentImage getParent() {
        return parent;
    }

    public void setParent(DocumentImage parent) {
        this.parent = parent;
    }
}