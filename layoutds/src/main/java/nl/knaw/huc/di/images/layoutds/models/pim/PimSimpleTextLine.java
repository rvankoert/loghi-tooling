package nl.knaw.huc.di.images.layoutds.models.pim;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.knaw.huc.di.images.layoutds.models.DocumentOCRResult;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"pageXmlLocation", "lineId"})
)
@Entity
public class PimSimpleTextLine implements IPimObject {
    private final Date created;
    private UUID uuid;
    private String pageXmlLocation;
    private String lineId;
    private String text;
    private String groundTruth;
    private Double confidence;
    private boolean skip;
    private Date updated;
    private Double lowestConf;
    @ManyToOne(targetEntity = DocumentOCRResult.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private DocumentOCRResult parent;
    private Integer width;
    private Integer height;

    public PimSimpleTextLine() {
        this.created = new Date();
        this.uuid = UUID.randomUUID();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Date getCreated() {
        return created;
    }

    public String getPageXmlLocation() {
        return pageXmlLocation;
    }

    public void setPageXmlLocation(String pageXmlLocation) {
        this.pageXmlLocation = pageXmlLocation;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getGroundTruth() {
        return groundTruth;
    }

    public void setGroundTruth(String groundTruth) {
        this.groundTruth = groundTruth;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public void setUpdated(Date date) {
        this.updated = date;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public void setLowestConf(Double lowestConf) {
        this.lowestConf = lowestConf;
    }

    public Double getLowestConf() {
        return lowestConf;
    }

    public DocumentOCRResult getParent() {
        return parent;
    }

    public void setParent(DocumentOCRResult parent) {
        this.parent = parent;
    }

    public UUID getParentUUID() {
        return parent != null ? parent.getUuid() : null;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getWidth() {
        return width;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getHeight() {
        return height;
    }
}
