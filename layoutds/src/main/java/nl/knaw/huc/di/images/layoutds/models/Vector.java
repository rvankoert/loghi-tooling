package nl.knaw.huc.di.images.layoutds.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "vectors",
        indexes = {
                @Index(columnList = "documentImageId", name = "vector_documentImageId_hidx")
        })

public class Vector implements IPimObject {
    private Date created;
    private String documentOCRResultLineUUID;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private UUID uuid;
    @Transient
    private float distance;
    @ManyToOne(targetEntity = DocumentImage.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "documentImageId")
    private DocumentImage parent;
    @Column(name = "documentImageId", insertable = false, updatable = false)
    private Integer documentImageId;
    @Column(name = "documentOCRResultLine_Id", insertable = false, updatable = false)
    private Integer documentOCRResultLineId;
    @ManyToOne(targetEntity = DocumentOCRResultLine.class, fetch = FetchType.EAGER)
    @JsonIgnore
    private DocumentOCRResultLine documentOCRResultLine;
    private String imageLocation;
    @ManyToOne(targetEntity = VectorModel.class, fetch = FetchType.EAGER)
    @JsonIgnore
    private VectorModel vectorModel;
    @JsonIgnore
    @Column(name = "vectormodel_id", insertable = false, updatable = false)
    private Long vectorModelId;

    public Vector() {
        this.uuid = UUID.randomUUID();
        this.created = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Integer getDocumentImageId() {
        return documentImageId;
    }

    public void setDocumentImageId(Integer documentImageId) {
        this.documentImageId = documentImageId;
    }

    public void setDocumentOCRResultLineId(Integer documentOCRResultLineId) {
        this.documentOCRResultLineId = documentOCRResultLineId;
    }

    public DocumentOCRResultLine getDocumentOCRResultLine() {
        return documentOCRResultLine;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public VectorModel getVectorModel() {
        return vectorModel;
    }

    public void setVectorModel(VectorModel vectorModel) {
        this.vectorModel = vectorModel;
    }

    public String getDocumentOCRResultLineUUID() {
        return documentOCRResultLineUUID != null ? documentOCRResultLineUUID : documentOCRResultLine.getUuid().toString();
    }

    public void setDocumentOCRResultLineUUID(String documentOCRResultLineUUID) {
        this.documentOCRResultLineUUID = documentOCRResultLineUUID;
    }

    public DocumentImage getParent() {
        return parent;
    }

    public void setParent(DocumentImage parent) {
        this.parent = parent;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getVectorModelId() {
        return vectorModelId;
    }
}
