package nl.knaw.huc.di.images.layoutds.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
//@Table(indexes = {
//        @Index(columnList = "imageLocation", name = "documentTextLineSnippetNew_imageLocation_hidx")
//})

public class DocumentTextLineSnippetNew implements IPimObject {
    public DocumentTextLineSnippetNew() {
        this.created = new Date();
        this.uuid = UUID.randomUUID();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity = DocumentOCRResult.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "documentOCRResultId")
    @JsonIgnore
    private DocumentOCRResult parent;

    // TextLineId from PageXml
    private String textLineId;

    // Where did we store the temporary image?
    @Column(unique = true)
    private String imageLocation;
    private int startX;
    private int startY;
    private int height;
    private int width;
    private Date created;

    private String tag;
    private boolean isValidation;
    @Type(type = "text")
    private String features;

    @Column(name = "documentOCRResultId", insertable = false, updatable = false)
    private Integer documentOCRResultId;


    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DocumentOCRResult getParent() {
        return parent;
    }

    public void setParent(DocumentOCRResult parent) {
        this.parent = parent;
    }

    public String getTextLineId() {
        return textLineId;
    }

    public void setTextLineId(String textLineId) {
        this.textLineId = textLineId;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isValidation() {
        return isValidation;
    }

    public void setValidation(boolean validation) {
        isValidation = validation;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }
}
